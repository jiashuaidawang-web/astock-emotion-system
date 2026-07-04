package com.astock.module.spider.ths;

import com.astock.module.spider.audit.DataSyncAuditService;
import com.astock.module.spider.checkpoint.SpiderCheckpointService;
import com.astock.module.spider.domain.entity.StockPlateDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDimensionRow;
import com.astock.module.spider.domain.entity.StockPlateRelationRow;
import com.astock.module.spider.enums.SpiderSourceType;
import com.astock.module.spider.service.SpiderClickHouseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.astock.module.spider.common.SpiderHttpClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThsSpiderServiceImpl implements ThsSpiderService {

    private static final String DAILY = "DAILY";
    private static final Pattern PLATE_CODE_PATTERN = Pattern.compile("/code/(\\d{6})");
    private static final Pattern STOCK_CODE_PATTERN = Pattern.compile("\\b(\\d{6})\\b");
    private static final By PLATE_MENU_ARROW = By.xpath(
            "//*[contains(@class,'nav') or contains(@class,'header') or self::body]//*[contains(normalize-space(),'板块')][1]");
    private static final By CONCEPT_EXPAND_ALL = By.xpath(
            "//a[contains(normalize-space(),'展开全部')]"
                    + "|//*[contains(@class,'cate') or contains(@class,'expand')][contains(normalize-space(),'展开全部')]"
                    + "|/html/body/div[2]/div[2]/a");

    private final SpiderHttpClient httpClient;
    private final SpiderClickHouseRepository clickHouseRepository;
    private final DataSyncAuditService auditService;
    private final SpiderCheckpointService checkpointService;
    private final ObjectMapper objectMapper;
    private final ThsBrowserProperties browserProperties;
    private final ThsProxyProvider proxyProvider;
    private final ThreadLocal<String> currentProxy = new ThreadLocal<>();

    @Override
    public Map<String, Object> syncDaily(LocalDate tradeDate) {
        int plateInserted = syncPlateDaily(tradeDate);
        int relationInserted = syncPlateRelations(tradeDate);
        return Map.of(
                "tradeDate", tradeDate.toString(),
                "source", "ths",
                "plateInserted", plateInserted,
                "relationInserted", relationInserted);
    }

    private int syncPlateDaily(LocalDate tradeDate) {
        String table = "stock_plate_daily_kline_ths";
        auditService.start(tradeDate, table, DAILY);
        int sourceTotal = 0;
        int fetched = 0;
        int inserted = 0;
        RuntimeException lastException = null;
        int maxAttempts = maxThsAttempts();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            WebDriver driver = null;
            String proxy = selectProxy(attempt);
            currentProxy.set(proxy);
            try {
                log.info("同花顺板块行情同步开始, attempt={}, proxy={}", attempt + 1, proxyLabel(proxy));
                driver = createDriver(proxy);
            for (ThsEndpoint endpoint : ThsEndpoint.values()) {
                List<ThsPlate> plates = fetchPlateList(driver, endpoint);
                sourceTotal += plates.size();
                for (ThsPlate plate : plates) {
                    if (checkpointService.completed(tradeDate, SpiderSourceType.THS.getCode(), table, plate.plateCode())) {
                        continue;
                    }
                    driver.get(plate.href());
                    sleep(900);
                    StockPlateDimensionRow dimension = toDimension(plate);
                    StockPlateDailyKlineRow daily = parsePlateDaily(driver, tradeDate, plate);
                    clickHouseRepository.insertPlateDimensions(List.of(dimension));
                    int pageInserted = clickHouseRepository.insertPlateDailyKline(List.of(daily));
                    fetched += 1;
                    inserted += pageInserted;
                    checkpointService.markCompleted(tradeDate, SpiderSourceType.THS.getCode(), table, plate.plateCode(),
                            1, 1, pageInserted,
                            Map.of("plateName", plate.plateName(), "plateType", plate.plateType(), "href", plate.href()));
                }
            }
            auditService.markFetched(tradeDate, table, DAILY, sourceTotal, fetched);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of("source", "ths"));
            return inserted;
        } catch (Exception e) {
                lastException = new IllegalStateException("同花顺板块行情同步失败", e);
                if (!shouldRotateProxy(e) || attempt >= maxAttempts - 1) {
                    auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "ths", "proxy", proxyLabel(proxy)));
                    throw lastException;
                }
                proxyProvider.reportBadProxy(proxy, e);
                log.warn("同花顺板块行情触发反爬/滑块/403/代理异常, 准备切换代理后续跑, attempt={}, proxy={}, error={}",
                        attempt + 1, proxyLabel(proxy), rootMessage(e));
                sleep(1500);
        } finally {
            quit(driver);
                currentProxy.remove();
            }
        }
        throw lastException == null ? new IllegalStateException("同花顺板块行情同步失败") : lastException;
    }

    private int syncPlateRelations(LocalDate tradeDate) {
        String table = "stock_plate_relation_ths";
        auditService.start(tradeDate, table, DAILY);
        int sourceTotal = 0;
        int fetched = 0;
        int inserted = 0;
        RuntimeException lastException = null;
        int maxAttempts = maxThsAttempts();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            WebDriver driver = null;
            String proxy = selectProxy(attempt);
            currentProxy.set(proxy);
            try {
                log.info("同花顺板块关系同步开始, attempt={}, proxy={}", attempt + 1, proxyLabel(proxy));
                driver = createDriver(proxy);
            for (ThsEndpoint endpoint : ThsEndpoint.values()) {
                List<ThsPlate> plates = fetchPlateList(driver, endpoint);
                for (ThsPlate plate : plates) {
                    int nextPage = checkpointService.nextPage(tradeDate, SpiderSourceType.THS.getCode(), table, plate.plateCode());
                    if (nextPage < 0) {
                        continue;
                    }
                    driver.get(plate.href());
                    sleep(900);
                    String cookie = buildCookieHeader(driver);
                    int totalPage = resolveTotalPage(driver);
                    int plateFetched = 0;
                    int plateInserted = 0;
                    for (int page = nextPage; page <= totalPage; page++) {
                        ThsPlatePage platePage = fetchRelationPage(driver, plate, page, totalPage, cookie);
                        List<StockPlateRelationRow> rows = platePage.rows().stream()
                                .map(row -> toRelation(tradeDate, plate, row))
                                .toList();
                        int pageInserted = clickHouseRepository.insertPlateRelations(rows);
                        plateFetched += rows.size();
                        plateInserted += pageInserted;
                        checkpointService.markPageSuccess(tradeDate, SpiderSourceType.THS.getCode(), table, plate.plateCode(),
                                page, totalPage, plateFetched, rows.size(), pageInserted,
                                Map.of("plateName", plate.plateName(), "plateType", plate.plateType()));
                        sleep(500);
                    }
                    checkpointService.markCompleted(tradeDate, SpiderSourceType.THS.getCode(), table, plate.plateCode(),
                            plateFetched, plateFetched, plateInserted,
                            Map.of("plateName", plate.plateName(), "plateType", plate.plateType()));
                    sourceTotal += plateFetched;
                    fetched += plateFetched;
                    inserted += plateInserted;
                }
            }
            auditService.markFetched(tradeDate, table, DAILY, sourceTotal, fetched);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of("source", "ths"));
            return inserted;
        } catch (Exception e) {
                lastException = new IllegalStateException("同花顺板块关系同步失败", e);
                if (!shouldRotateProxy(e) || attempt >= maxAttempts - 1) {
                    auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "ths", "proxy", proxyLabel(proxy)));
                    throw lastException;
                }
                proxyProvider.reportBadProxy(proxy, e);
                log.warn("同花顺触发反爬/滑块/403/代理异常, 准备切换代理后续跑, attempt={}, proxy={}, error={}",
                        attempt + 1, proxyLabel(proxy), rootMessage(e));
                sleep(1500);
        } finally {
            quit(driver);
                currentProxy.remove();
            }
        }
        throw lastException == null ? new IllegalStateException("同花顺板块关系同步失败") : lastException;
    }

    private List<ThsPlate> fetchPlateList(WebDriver driver, ThsEndpoint endpoint) {
        driver.get(endpoint.getUrl());
        sleep(1200);
        touchPlateMenu(driver);
        if (endpoint.isNeedExpandAll()) {
            clickConceptExpandAll(driver);
        }
        Map<String, ThsPlate> plates = new LinkedHashMap<>();
        List<WebElement> links = driver.findElements(By.cssSelector(
                ".cate_items a[href*='/detail/code/'], "
                        + ".cate_group a[href*='/detail/code/'], "
                        + ".cate_inner a[href*='/detail/code/'], "
                        + "a[href*='/detail/code/']"));
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            String name = link.getText();
            String code = extractPlateCode(href);
            if (StringUtils.hasText(code) && StringUtils.hasText(name)) {
                plates.put(code, new ThsPlate(endpoint.getPlateType(), code, name.trim(), href, endpoint.getRelationSortField()));
            }
        }
        if (plates.isEmpty()) {
            throw new IllegalStateException("同花顺板块列表为空: endpoint=" + endpoint.name()
                    + ", url=" + endpoint.getUrl()
                    + ", currentUrl=" + driver.getCurrentUrl()
                    + ", title=" + driver.getTitle()
                    + ", pageText=" + safeBodyText(driver));
        }
        log.info("同花顺{}板块列表抓取完成, count={}, url={}", endpoint.getPlateTypeName(), plates.size(), endpoint.getUrl());
        return new ArrayList<>(plates.values());
    }

    private StockPlateDailyKlineRow parsePlateDaily(WebDriver driver, LocalDate tradeDate, ThsPlate plate) {
        StockPlateDailyKlineRow row = new StockPlateDailyKlineRow();
        row.setTradeDate(tradeDate);
        row.setType(SpiderSourceType.THS.getCode());
        row.setPlateType(plate.plateType());
        row.setPlateCode(plate.plateCode());
        row.setPlateName(plate.plateName());
        row.setClosePrice(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[1]/span",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[1]/span",
                "html/body/div[2]/div[3]/div[2]/div[1]/div[2]/div[1]/div[1]/span")));
        row.setPctChange(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[6]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[6]/dd",
                "html/body/div[2]/div[3]/div[2]/div[1]/div[2]/div[1]/div[2]/dl[6]/dd")));
        row.setOpenPrice(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[1]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[1]/dd")));
        row.setPreClosePrice(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[2]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[2]/dd")));
        row.setLowPrice(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[3]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[3]/dd")));
        row.setHighPrice(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[4]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[4]/dd")));
        row.setVolume(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[5]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[5]/dd")).multiply(new BigDecimal("10000")).longValue());
        row.setTurnoverAmount(decimal(firstText(driver,
                "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[10]/dd",
                "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[10]/dd")).multiply(new BigDecimal("100000000")));
        row.setChangeAmount(BigDecimal.ZERO);
        row.setAmplitude(BigDecimal.ZERO);
        row.setVolumeRatio(BigDecimal.ZERO);
        row.setTurnoverRate(BigDecimal.ZERO);
        row.setTotalMarketValue(BigDecimal.ZERO);
        row.setFloatMarketValue(BigDecimal.ZERO);
        row.setFeatures(features(Map.of(
                "href", plate.href(),
                "source", "ths",
                "rawTitle", driver.getTitle(),
                "increaseRank", firstText(driver,
                        "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[7]/dd",
                        "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[7]/dd"),
                "upCount", firstText(driver,
                        "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[8]/dd/span[1]",
                        "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[8]/dd/span[1]"),
                "downCount", firstText(driver,
                        "/html/body/div[2]/div[3]/div[2]/div/div[2]/div[1]/div[2]/dl[8]/dd/span[2]",
                        "/html/body/div[2]/div[2]/div[2]/div/div/div[1]/div[2]/dl[8]/dd/span[2]"))));
        return row;
    }

    private ThsPlatePage fetchRelationPage(WebDriver driver, ThsPlate plate, int page, int totalPage, String cookie) {
        String url = buildRelationUrl(plate, page);
        String html = fetchRelationHtml(driver, plate, url, cookie);
        Document document = Jsoup.parse(html);
        List<ThsStockRow> rows = new ArrayList<>();
        for (Element tr : document.select("tbody tr")) {
            String text = tr.text();
            Matcher matcher = STOCK_CODE_PATTERN.matcher(text);
            if (!matcher.find()) {
                continue;
            }
            String stockCode = matcher.group(1);
            String stockName = "";
            List<Element> links = tr.select("a");
            for (Element link : links) {
                String linkText = link.text();
                if (StringUtils.hasText(linkText) && !linkText.matches("\\d{6}")) {
                    stockName = linkText.trim();
                    break;
                }
            }
            rows.add(new ThsStockRow(stockCode, stockName));
        }
        return new ThsPlatePage(rows.size(), totalPage, page, rows);
    }

    private String fetchRelationHtml(WebDriver driver, ThsPlate plate, String url, String cookie) {
        try {
            return httpClient.getByUrlConnection(url, thsAjaxHeaders(plate, cookie), currentProxy.get());
        } catch (Exception e) {
            log.warn("同花顺Ajax请求失败, 改用Selenium打开Ajax页, plateCode={}, url={}, error={}",
                    plate.plateCode(), url, e.getMessage());
            driver.get(url);
            sleep(800);
            String html = driver.getPageSource();
            if (html != null && html.contains("Nginx forbidden")) {
                throw new IllegalStateException("同花顺Ajax Selenium访问仍被拒绝, plateCode="
                        + plate.plateCode() + ", url=" + url + ", pageText=" + safeBodyText(driver));
            }
            return html == null ? "" : html;
        }
    }

    private Map<String, String> thsAjaxHeaders(ThsPlate plate, String cookie) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Cookie", cookie);
        headers.put("Host", "q.10jqka.com.cn");
        headers.put("Referer", plate.href());
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        return headers;
    }

    private String buildRelationUrl(ThsPlate plate, int page) {
        String href = plate.href();
        String[] parts = href.split("code", 2);
        if (parts.length == 2) {
            return parts[0] + "field/" + plate.relationSortField() + "/order/desc/page/" + page + "/ajax/1/code" + parts[1];
        }
        return href + "field/" + plate.relationSortField() + "/order/desc/page/" + page + "/ajax/1/";
    }

    private StockPlateDimensionRow toDimension(ThsPlate plate) {
        StockPlateDimensionRow row = new StockPlateDimensionRow();
        row.setType(SpiderSourceType.THS.getCode());
        row.setPlateType(plate.plateType());
        row.setPlateCode(plate.plateCode());
        row.setPlateName(plate.plateName());
        row.setFeatures(features(Map.of("href", plate.href(), "source", "ths")));
        return row;
    }

    private StockPlateRelationRow toRelation(LocalDate tradeDate, ThsPlate plate, ThsStockRow stock) {
        StockPlateRelationRow row = new StockPlateRelationRow();
        row.setTradeDate(tradeDate);
        row.setType(SpiderSourceType.THS.getCode());
        row.setPlateType(plate.plateType());
        row.setPlateCode(plate.plateCode());
        row.setPlateName(plate.plateName());
        row.setStockCode(stock.stockCode());
        row.setStockName(stock.stockName());
        row.setExchange(exchange(stock.stockCode()));
        row.setFeatures(features(Map.of("href", plate.href(), "source", "ths")));
        return row;
    }

    private int resolveTotalPage(WebDriver driver) {
        String pageInfo = firstText(driver, "//*[contains(@class,'page_info')]");
        if (!StringUtils.hasText(pageInfo) || !pageInfo.contains("/")) {
            return 1;
        }
        String total = pageInfo.substring(pageInfo.lastIndexOf('/') + 1).replaceAll("[^0-9]", "");
        return StringUtils.hasText(total) ? Integer.parseInt(total) : 1;
    }

    private String buildCookieHeader(WebDriver driver) {
        List<String> values = new ArrayList<>();
        for (Cookie cookie : driver.manage().getCookies()) {
            values.add(cookie.getName() + "=" + cookie.getValue());
        }
        return String.join("; ", values);
    }

    private WebDriver createDriver(String proxyAddress) {
        ChromeOptions options = new ChromeOptions();
        if (browserProperties.isHeadless()) {
            options.addArguments("--headless=new");
        }
        if (browserProperties.getChromeBinary() != null && !browserProperties.getChromeBinary().isBlank()) {
            options.setBinary(browserProperties.getChromeBinary());
        }
        if (browserProperties.getArguments() != null) {
            options.addArguments(browserProperties.getArguments());
        }
        if (StringUtils.hasText(proxyAddress)) {
            options.addArguments("--proxy-server=http://" + normalizeProxy(proxyAddress));
        }
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        if (browserProperties.getRemoteUrl() != null && !browserProperties.getRemoteUrl().isBlank()) {
            try {
                WebDriver driver = new RemoteWebDriver(new URL(browserProperties.getRemoteUrl()), options);
                prepareDriver(driver);
                return driver;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid spider.ths.browser.remote-url: " + browserProperties.getRemoteUrl(), e);
            }
        }
        if (browserProperties.getChromeDriver() != null && !browserProperties.getChromeDriver().isBlank()) {
            System.setProperty("webdriver.chrome.driver", browserProperties.getChromeDriver());
        }
        WebDriver driver = new ChromeDriver(options);
        prepareDriver(driver);
        return driver;
    }

    private String selectProxy(int attempt) {
        List<String> proxies = normalizedProxyPool();
        if (proxies.isEmpty()) {
            if (browserProperties.isDirectFirst()) {
                return "";
            }
            throw new IllegalStateException("同花顺代理池未就绪: 健康代理数量为0，请先等待代理池拉取和探测完成");
        }
        if (browserProperties.isDirectFirst()) {
            if (attempt == 0) {
                return "";
            }
            return proxies.get((attempt - 1) % proxies.size());
        }
        return proxies.get(attempt % proxies.size());
    }

    private List<String> normalizedProxyPool() {
        return proxyProvider.availableProxies().stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeProxy)
                .distinct()
                .toList();
    }

    private String normalizeProxy(String proxyAddress) {
        return proxyAddress
                .replace("http://", "")
                .replace("https://", "")
                .trim();
    }

    private int maxThsAttempts() {
        int proxyCount = normalizedProxyPool().size();
        int retryCount = Math.max(0, browserProperties.getMaxProxyRetries());
        int configuredAttempts = 1 + retryCount;
        if (proxyCount == 0) {
            if (!browserProperties.isDirectFirst()) {
                throw new IllegalStateException("同花顺代理池未就绪: 健康代理数量为0，请先等待代理池拉取和探测完成");
            }
            return 1;
        }
        if (browserProperties.isDirectFirst()) {
            return Math.min(configuredAttempts, proxyCount + 1);
        }
        return Math.min(configuredAttempts, proxyCount);
    }

    private boolean shouldRotateProxy(Throwable throwable) {
        String message = rootMessage(throwable).toLowerCase();
        return message.contains("403")
                || message.contains("forbidden")
                || message.contains("nginx forbidden")
                || message.contains("滑块")
                || message.contains("验证码")
                || message.contains("验证")
                || message.contains("captcha")
                || message.contains("proxy")
                || message.contains("timed out")
                || message.contains("timeout")
                || message.contains("connection refused")
                || message.contains("connection reset")
                || message.contains("unreachable")
                || message.contains("err_proxy")
                || message.contains("net::err")
                || message.contains("tunnel")
                || message.contains("failed to establish");
    }

    private String rootMessage(Throwable throwable) {
        Throwable cursor = throwable;
        String message = "";
        while (cursor != null) {
            if (StringUtils.hasText(cursor.getMessage())) {
                message = cursor.getMessage();
            }
            cursor = cursor.getCause();
        }
        return message;
    }

    private String proxyLabel(String proxy) {
        return StringUtils.hasText(proxy) ? normalizeProxy(proxy) : "DIRECT";
    }

    private void prepareDriver(WebDriver driver) {
        try {
            driver.manage().window().maximize();
        } catch (Exception ignored) {
            // Remote headless Chrome may not support maximize in every environment.
        }
    }

    private void touchPlateMenu(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(PLATE_MENU_ARROW));
            scrollIntoView(driver, menu);
            new Actions(driver).moveToElement(menu).pause(Duration.ofMillis(300)).perform();
            sleep(500);
        } catch (Exception ignored) {
            // Some pages already render the category list without hovering the top menu.
        }
    }

    private void clickConceptExpandAll(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement expand = wait.until(ExpectedConditions.presenceOfElementLocated(CONCEPT_EXPAND_ALL));
            scrollIntoView(driver, expand);
            try {
                expand.click();
            } catch (Exception clickFailed) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", expand);
            }
            sleep(800);
        } catch (Exception ignored) {
            // If the page has already expanded, the next selector pass will still find all links.
        }
    }

    private void scrollIntoView(WebDriver driver, WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        } catch (Exception ignored) {
            // Best-effort helper for headless layout.
        }
    }

    private String safeBodyText(WebDriver driver) {
        try {
            String text = driver.findElement(By.tagName("body")).getText();
            if (text == null) {
                return "";
            }
            return text.length() <= 500 ? text : text.substring(0, 500);
        } catch (Exception e) {
            return "";
        }
    }

    private String firstText(WebDriver driver, String... xpaths) {
        for (String xpath : xpaths) {
            try {
                WebElement element = driver.findElement(By.xpath(xpath));
                if (element != null && StringUtils.hasText(element.getText())) {
                    return element.getText().trim();
                }
            } catch (Exception ignored) {
                // Try the next known page layout.
            }
        }
        return "";
    }

    private void clickIfExists(WebDriver driver, By by) {
        try {
            driver.findElement(by).click();
        } catch (Exception ignored) {
            // The button is not present on every board page.
        }
    }

    private String extractPlateCode(String href) {
        if (!StringUtils.hasText(href)) {
            return "";
        }
        Matcher matcher = PLATE_CODE_PATTERN.matcher(href);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String exchange(String stockCode) {
        if (!StringUtils.hasText(stockCode)) {
            return "";
        }
        return stockCode.startsWith("6") ? "SH" : "SZ";
    }

    private BigDecimal decimal(String value) {
        if (!StringUtils.hasText(value) || "-".equals(value.trim())) {
            return BigDecimal.ZERO;
        }
        String normalized = value.replace("%", "").replace(",", "").trim();
        BigDecimal multiplier = BigDecimal.ONE;
        if (normalized.contains("亿")) {
            multiplier = new BigDecimal("100000000");
            normalized = normalized.replace("亿", "");
        } else if (normalized.contains("万")) {
            multiplier = new BigDecimal("10000");
            normalized = normalized.replace("万", "");
        }
        normalized = normalized.replaceAll("[^0-9.\\-]", "");
        if (!StringUtils.hasText(normalized) || "-".equals(normalized)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalized).multiply(multiplier);
    }

    private String features(Map<String, Object> features) {
        try {
            return objectMapper.writeValueAsString(features);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void quit(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}
