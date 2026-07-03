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
        WebDriver driver = null;
        try {
            driver = createDriver();
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
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "ths"));
            throw new IllegalStateException("同花顺板块行情同步失败", e);
        } finally {
            quit(driver);
        }
    }

    private int syncPlateRelations(LocalDate tradeDate) {
        String table = "stock_plate_relation_ths";
        auditService.start(tradeDate, table, DAILY);
        int sourceTotal = 0;
        int fetched = 0;
        int inserted = 0;
        WebDriver driver = null;
        try {
            driver = createDriver();
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
                        ThsPlatePage platePage = fetchRelationPage(plate, page, totalPage, cookie);
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
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "ths"));
            throw new IllegalStateException("同花顺板块关系同步失败", e);
        } finally {
            quit(driver);
        }
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

    private ThsPlatePage fetchRelationPage(ThsPlate plate, int page, int totalPage, String cookie) {
        String url = buildRelationUrl(plate, page);
        String html = httpClient.get(url, Map.of(
                "Cookie", cookie,
                "Host", "q.10jqka.com.cn",
                "Referer", plate.href()));
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

    private WebDriver createDriver() {
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
