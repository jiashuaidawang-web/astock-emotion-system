package com.astock.module.spider.dc;

import com.astock.module.spider.common.JsonpParser;
import com.astock.module.spider.common.SpiderHttpClient;
import com.astock.module.spider.dc.model.DcPageResult;
import com.astock.module.spider.dc.model.DcPoolResult;
import com.astock.module.spider.dc.model.DcSinglePageResult;
import com.astock.module.spider.ths.ThsProxyProvider;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DcApiClient {

    private static final int PAGE_SIZE = 100;
    private static final int MAX_HTTP_ROUNDS = 2;
    private static final int MAX_PROXY_TRIES = 5;
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String PLATE_STOCK_URL = "https://push2.eastmoney.com/weblogin/api/qt/clist/get?np=1&fltt=1&invt=2&cb=jQuery37103197788499441794_1782544819942&fs=b%%3Abk%s%%2Bf%%3A!50&fields=f12%%2Cf13%%2Cf14%%2Cf1%%2Cf2%%2Cf4%%2Cf3%%2Cf152%%2Cf5%%2Cf6%%2Cf7%%2Cf15%%2Cf18%%2Cf16%%2Cf17%%2Cf10%%2Cf8%%2Cf9%%2Cf23&fid=f3&pn=%d&pz=100&po=1&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=3308306092050116%%7C0%%7C1%%7C0%%7Cweb&_=%d";

    private final SpiderHttpClient httpClient;
    private final JsonpParser jsonpParser;
    private final ThsProxyProvider thsProxyProvider;

    public DcPageResult fetchPaged(DcEndpoint endpoint) {
        return fetchPaged(page -> String.format(endpoint.getUrlTemplate(), page, System.currentTimeMillis()));
    }

    public DcSinglePageResult fetchPage(DcEndpoint endpoint, int page) {
        return fetchPage(page, String.format(endpoint.getUrlTemplate(), page, System.currentTimeMillis()));
    }

    public DcPageResult fetchPlateStocks(String plateCode) {
        String bkNumber = plateCode == null ? "" : plateCode.replace("BK", "");
        return fetchPaged(page -> String.format(PLATE_STOCK_URL, bkNumber, page, System.currentTimeMillis()));
    }

    public DcSinglePageResult fetchPlateStocksPage(String plateCode, int page) {
        String bkNumber = plateCode == null ? "" : plateCode.replace("BK", "");
        return fetchPage(page, String.format(PLATE_STOCK_URL, bkNumber, page, System.currentTimeMillis()));
    }

    public DcPoolResult fetchPool(DcEndpoint endpoint, LocalDate tradeDate) {
        String url = String.format(endpoint.getUrlTemplate(), tradeDate.format(BASIC_DATE), System.currentTimeMillis());
        try {
            JsonNode root = jsonpParser.parse(fetchEastMoney(url, endpoint.name()));
            JsonNode data = root.path("data");
            int total = data.path("tc").asInt(0);
            LocalDate queryDate = parseQueryDate(data.path("qdate").asText(""));
            List<JsonNode> rows = new ArrayList<>();
            data.path("pool").forEach(rows::add);
            return new DcPoolResult(total, queryDate == null ? tradeDate : queryDate, rows);
        } catch (IOException e) {
            throw new IllegalStateException("东方财富股票池接口解析失败: " + endpoint.name(), e);
        }
    }

    private DcPageResult fetchPaged(PageUrlBuilder urlBuilder) {
        int total = 0;
        List<JsonNode> allRows = new ArrayList<>();
        int totalPage = 1;
        for (int page = 1; page <= totalPage; page++) {
            try {
                String url = urlBuilder.build(page);
                JsonNode root = jsonpParser.parse(fetchEastMoney(url, "paged-" + page));
                JsonNode data = root.path("data");
                total = data.path("total").asInt(total);
                totalPage = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
                data.path("diff").forEach(allRows::add);
            } catch (IOException e) {
                throw new IllegalStateException("东方财富分页接口解析失败, page=" + page, e);
            }
        }
        return new DcPageResult(total, allRows);
    }

    private DcSinglePageResult fetchPage(int page, String url) {
        try {
            JsonNode root = jsonpParser.parse(fetchEastMoney(url, "page-" + page));
            JsonNode data = root.path("data");
            int total = data.path("total").asInt(0);
            int totalPage = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            List<JsonNode> rows = new ArrayList<>();
            data.path("diff").forEach(rows::add);
            return new DcSinglePageResult(total, totalPage, page, rows);
        } catch (IOException e) {
            throw new IllegalStateException("东方财富分页接口解析失败, page=" + page, e);
        }
    }

    private String fetchEastMoney(String url, String scene) {
        RuntimeException lastException = null;
        List<String> proxies = thsProxyProvider.reusableProxies();
        List<String> requestUrls = eastMoneyUrlVariants(url);
        for (int round = 1; round <= MAX_HTTP_ROUNDS; round++) {
            for (String requestUrl : requestUrls) {
                for (DcRequestStrategy strategy : DcRequestStrategy.values()) {
                    for (String proxy : proxyAttempts(proxies, strategy.proxySupported())) {
                        try {
                            String body = executeStrategy(strategy, requestUrl, proxy);
                            if (body != null && !body.isBlank()) {
                                log.info("东方财富接口请求成功, scene={}, strategy={}, proxy={}, url={}",
                                        scene, strategy.name(), proxyLabel(proxy), requestUrl);
                                return body;
                            }
                            lastException = new IllegalStateException("东方财富接口返回空内容");
                        } catch (RuntimeException e) {
                            lastException = e;
                        }
                        log.warn("东方财富接口请求失败, scene={}, round={}/{}, strategy={}, proxy={}, url={}, error={}",
                                scene, round, MAX_HTTP_ROUNDS, strategy.name(), proxyLabel(proxy), requestUrl,
                                lastException.getMessage());
                    }
                }
            }
            sleep(Math.min(2_000L, 300L * round));
        }
        throw new IllegalStateException("东方财富接口连续失败, scene=" + scene + ", url=" + url, lastException);
    }

    private String executeStrategy(DcRequestStrategy strategy, String url, String proxy) {
        return switch (strategy) {
            case URL_CONN_LEGACY -> httpClient.getByUrlConnection(url, eastMoneyLegacyHeaders(), proxy);
            case URL_CONN_BROWSER -> httpClient.getByUrlConnection(url, eastMoneyBrowserHeaders(url, false), proxy);
            case URL_CONN_BROWSER_WITH_HOST -> httpClient.getByUrlConnection(url, eastMoneyBrowserHeaders(url, true), proxy);
            case REST_TEMPLATE_BROWSER -> httpClient.get(url, eastMoneyBrowserHeaders(url, false), proxy);
            case JDK_HTTP_CLIENT -> getByJdkHttpClient(url, eastMoneyBrowserHeaders(url, false), proxy);
        };
    }

    private List<String> eastMoneyUrlVariants(String url) {
        Set<String> urls = new LinkedHashSet<>();
        urls.add(url);
        if (url.startsWith("http://83.push2.eastmoney.com")) {
            urls.add(url.replaceFirst("http://83\\.push2\\.eastmoney\\.com", "http://push2.eastmoney.com"));
            urls.add(url.replaceFirst("http://83\\.push2\\.eastmoney\\.com", "https://push2.eastmoney.com"));
        } else if (url.startsWith("http://81.push2.eastmoney.com")) {
            urls.add(url.replaceFirst("http://81\\.push2\\.eastmoney\\.com", "http://push2.eastmoney.com"));
            urls.add(url.replaceFirst("http://81\\.push2\\.eastmoney\\.com", "https://push2.eastmoney.com"));
        } else if (url.startsWith("http://push2.eastmoney.com")) {
            urls.add(url.replaceFirst("http://push2\\.eastmoney\\.com", "https://push2.eastmoney.com"));
        } else if (url.startsWith("https://push2.eastmoney.com")) {
            urls.add(url.replaceFirst("https://push2\\.eastmoney\\.com", "http://push2.eastmoney.com"));
        }
        return new ArrayList<>(urls);
    }

    private List<String> proxyAttempts(List<String> proxies, boolean proxySupported) {
        List<String> values = new ArrayList<>();
        values.add("");
        if (!proxySupported || proxies == null || proxies.isEmpty()) {
            return values;
        }
        proxies.stream()
                .filter(proxy -> proxy != null && !proxy.isBlank())
                .distinct()
                .limit(MAX_PROXY_TRIES)
                .forEach(values::add);
        return values;
    }

    private String proxyLabel(String proxy) {
        return proxy == null || proxy.isBlank() ? "DIRECT" : proxy;
    }

    private Map<String, String> eastMoneyLegacyHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("User-Agent", "Mozilla/5.0");
        headers.put("Accept", "*/*");
        headers.put("Connection", "close");
        return headers;
    }

    private Map<String, String> eastMoneyBrowserHeaders(String url, boolean includeHost) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "application/json,text/javascript,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.put("Cache-Control", "no-cache");
        headers.put("Connection", "close");
        headers.put("Pragma", "no-cache");
        headers.put("Origin", "http://quote.eastmoney.com");
        headers.put("Referer", "http://quote.eastmoney.com/center/gridlist.html#hs_a_board");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        if (!includeHost) {
            return headers;
        }
        if (url.startsWith("https://push2.eastmoney.com") || url.startsWith("http://push2.eastmoney.com")) {
            headers.put("Host", "push2.eastmoney.com");
        } else if (url.startsWith("https://push2ex.eastmoney.com")) {
            headers.put("Host", "push2ex.eastmoney.com");
        } else if (url.startsWith("http://83.push2.eastmoney.com")) {
            headers.put("Host", "83.push2.eastmoney.com");
        } else if (url.startsWith("http://81.push2.eastmoney.com")) {
            headers.put("Host", "81.push2.eastmoney.com");
        }
        return headers;
    }

    private String getByJdkHttpClient(String url, Map<String, String> headers, String proxy) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET();
            headers.forEach((name, value) -> {
                if (!"Host".equalsIgnoreCase(name) && !"Connection".equalsIgnoreCase(name)) {
                    requestBuilder.header(name, value);
                }
            });
            java.net.http.HttpClient.Builder clientBuilder = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .version(java.net.http.HttpClient.Version.HTTP_1_1);
            if (proxy != null && !proxy.isBlank()) {
                String[] parts = proxy.replace("http://", "").replace("https://", "").trim().split(":", 2);
                if (parts.length == 2) {
                    clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(parts[0], Integer.parseInt(parts[1]))));
                }
            }
            java.net.http.HttpClient client = clientBuilder.build();
            HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("JDK HttpClient failed, status=" + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("JDK HttpClient failed, url=" + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("JDK HttpClient interrupted, url=" + url, e);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private LocalDate parseQueryDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, BASIC_DATE);
    }

    private interface PageUrlBuilder {
        String build(int page);
    }

    private enum DcRequestStrategy {
        URL_CONN_LEGACY(true),
        URL_CONN_BROWSER(true),
        URL_CONN_BROWSER_WITH_HOST(true),
        REST_TEMPLATE_BROWSER(true),
        JDK_HTTP_CLIENT(true);

        private final boolean proxySupported;

        DcRequestStrategy(boolean proxySupported) {
            this.proxySupported = proxySupported;
        }

        private boolean proxySupported() {
            return proxySupported;
        }
    }
}
