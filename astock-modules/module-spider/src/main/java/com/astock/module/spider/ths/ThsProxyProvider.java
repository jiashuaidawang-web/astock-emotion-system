package com.astock.module.spider.ths;

import com.astock.module.spider.common.SpiderHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThsProxyProvider {

    private static final Pattern PROXY_PATTERN = Pattern.compile(
            "(?<![\\d.])(?:https?://)?((?:\\d{1,3}\\.){3}\\d{1,3}:\\d{2,5})(?!\\d)");
    private static final Pattern IP_PORT_COLUMNS_PATTERN = Pattern.compile(
            "(?<![\\d.])((?:\\d{1,3}\\.){3}\\d{1,3})\\s+(\\d{2,5})(?!\\d)");
    private static final Set<String> BLOCK_WORDS = Set.of(
            "nginx forbidden", "forbidden", "captcha", "验证码", "滑块", "访问过于频繁", "blocked");
    private static final List<String> DEFAULT_PROVIDER_URLS = List.of(
            "https://api.openproxylist.xyz/http.txt",
            "https://www.qiyunip.com/freeProxy",
            "https://www.89ip.cn/"
    );

    private final ThsBrowserProperties properties;
    private final SpiderHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final AtomicBoolean refreshing = new AtomicBoolean(false);
    private volatile List<String> candidateProxies = List.of();
    private volatile List<String> healthyProxies = List.of();
    private volatile Instant lastProviderFetchTime = Instant.EPOCH;

    public List<String> availableProxies() {
        ensureMinimumUsable(healthyProxies.isEmpty());
        if (healthyProxies.isEmpty() && refreshing.get()) {
            waitForWarmup();
        }
        return healthyProxies;
    }

    public void refreshNow() {
        ensureMinimumUsable(true);
    }

    public Map<String, Object> warmup() {
        ensureMinimumUsable(true);
        List<String> snapshot = healthyProxies;
        return Map.of(
                "healthyCount", snapshot.size(),
                "minUsableCount", Math.max(0, properties.getMinUsableProxyCount()),
                "maxPoolSize", Math.max(1, properties.getMaxProxyPoolSize()),
                "providerUrls", providerUrls(),
                "proxies", snapshot);
    }

    public List<String> reusableProxies() {
        List<String> healthySnapshot = healthyProxies;
        if (!healthySnapshot.isEmpty()) {
            return healthySnapshot;
        }
        return candidateProxies;
    }

    public Map<String, Object> candidates() {
        List<Map<String, Object>> providers = new ArrayList<>();
        Set<String> total = new LinkedHashSet<>();
        for (String configuredUrl : providerUrls()) {
            Set<String> providerProxies = new LinkedHashSet<>();
            List<String> expandedUrls = expandProviderUrls(configuredUrl);
            for (String providerUrl : expandedUrls) {
                try {
                    String body = httpClient.getByUrlConnection(providerUrl);
                    Set<String> parsed = parseProxies(body);
                    providerProxies.addAll(parsed);
                    providers.add(Map.of(
                            "url", providerUrl,
                            "success", true,
                            "parsedCount", parsed.size(),
                            "sample", parsed.stream().limit(20).toList()));
                } catch (Exception e) {
                    providers.add(Map.of(
                            "url", providerUrl,
                            "success", false,
                            "error", e.getMessage() == null ? "" : e.getMessage()));
                }
            }
            total.addAll(providerProxies);
        }
        return Map.of(
                "candidateCount", total.size(),
                "sample", total.stream().limit(50).toList(),
                "providers", providers);
    }

    public void reportBadProxy(String proxyAddress, Throwable throwable) {
        if (!StringUtils.hasText(proxyAddress)) {
            return;
        }
        String normalized = normalize(proxyAddress);
        synchronized (this) {
            if (!healthyProxies.contains(normalized)) {
                return;
            }
            List<String> next = new ArrayList<>(healthyProxies);
            next.remove(normalized);
            healthyProxies = List.copyOf(next);
        }
        log.warn("同花顺代理已剔除, proxy={}, remaining={}, reason={}",
                normalized, healthyProxies.size(), throwable == null ? "unknown" : rootMessage(throwable));
        ensureMinimumUsable(false);
    }

    @Scheduled(
            fixedDelayString = "${spider.ths.browser.proxy-pool-monitor-delay-ms:60000}",
            initialDelayString = "${spider.ths.browser.proxy-pool-initial-delay-ms:15000}")
    public void maintainHealthyPool() {
        ensureMinimumUsable(false);
    }

    @Scheduled(
            fixedDelayString = "${spider.ths.browser.proxy-pool-log-delay-ms:5000}",
            initialDelayString = "${spider.ths.browser.proxy-pool-log-delay-ms:5000}")
    public void logHealthyPool() {
        if (!properties.isProxyPoolLogEnabled()) {
            return;
        }
        List<String> candidateSnapshot = candidateProxies;
        List<String> snapshot = healthyProxies;
        int limit = Math.max(0, properties.getCandidateProxyLogLimit());
        log.info("同花顺内存健康代理池实时状态, count={}, proxies={}",
                snapshot.size(), snapshot.isEmpty() ? "[]" : String.join(",", snapshot));
        log.info("同花顺内存候选代理池实时状态, count={}, printLimit={}, proxies={}",
                candidateSnapshot.size(), limit,
                candidateSnapshot.isEmpty() ? "[]" : String.join(",", candidateSnapshot.stream().limit(limit).toList()));
    }

    private void ensureMinimumUsable(boolean force) {
        int minUsable = Math.max(0, properties.getMinUsableProxyCount());
        if (!force && healthyProxies.size() >= minUsable && !cacheExpired()) {
            return;
        }
        if (!refreshing.compareAndSet(false, true)) {
            if (healthyProxies.isEmpty()) {
                waitForWarmup();
            }
            return;
        }
        try {
            refreshHealthyPool(force, minUsable);
        } finally {
            refreshing.set(false);
        }
    }

    private synchronized void refreshHealthyPool(boolean force, int minUsable) {
        if (!force && healthyProxies.size() >= minUsable && !cacheExpired()) {
            return;
        }
        List<String> before = healthyProxies;
        Set<String> candidates = new LinkedHashSet<>(before);
        candidates.addAll(fetchCandidates(force));
        log.info("同花顺代理候选池拉取完成, candidates={}, beforeHealthy={}, providers={}",
                candidates.size(), before.size(), providerUrls());

        List<String> verified = new ArrayList<>();
        int maxPoolSize = Math.max(1, properties.getMaxProxyPoolSize());
        for (String candidate : before) {
            if (verified.size() >= maxPoolSize) {
                break;
            }
            String proxy = normalize(candidate);
            if (isValidProxy(proxy)) {
                verified.add(proxy);
            }
        }
        int targetCount = maxPoolSize;
        if (verified.size() < targetCount) {
            List<String> needTest = candidates.stream()
                    .map(this::normalize)
                    .filter(this::isValidProxy)
                    .filter(proxy -> !verified.contains(proxy))
                    .limit(Math.max(targetCount, properties.getProxyTestCandidateLimit()))
                    .toList();
            verified.addAll(testProxiesConcurrently(needTest, targetCount - verified.size()));
        }
        healthyProxies = List.copyOf(verified);
        log.info("同花顺健康代理池维护完成, before={}, after={}, minUsable={}, concurrency={}, providerConfigured={}",
                before.size(), healthyProxies.size(), minUsable,
                Math.max(1, properties.getProxyTestConcurrency()), !providerUrls().isEmpty());
    }

    private List<String> testProxiesConcurrently(List<String> candidates, int needCount) {
        if (needCount <= 0 || candidates.isEmpty()) {
            return List.of();
        }
        int concurrency = Math.max(1, properties.getProxyTestConcurrency());
        int testLimit = Math.max(needCount, properties.getProxyTestCandidateLimit());
        List<String> batch = candidates.stream().limit(testLimit).toList();
        List<String> verified = new ArrayList<>();
        Instant start = Instant.now();
        log.info("同花顺代理并发探测开始, candidates={}, need={}, concurrency={}, timeoutMs={}",
                batch.size(), needCount, concurrency, Math.max(1_000, properties.getProxyTestTimeoutMs()));

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
            int submitted = 0;
            int completed = 0;
            for (String proxy : batch) {
                if (submitted - completed >= concurrency) {
                    String passed = takeProxyProbeResult(completionService);
                    completed++;
                    if (StringUtils.hasText(passed)) {
                        verified.add(passed);
                        if (verified.size() >= needCount) {
                            executor.shutdownNow();
                            break;
                        }
                    }
                }
                completionService.submit(() -> testProxy(proxy) ? proxy : "");
                submitted++;
            }
            while (completed < submitted && verified.size() < needCount) {
                String passed = takeProxyProbeResult(completionService);
                completed++;
                if (StringUtils.hasText(passed)) {
                    verified.add(passed);
                }
            }
        } finally {
            executor.shutdownNow();
        }

        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        log.info("同花顺代理并发探测完成, tested={}, passed={}, need={}, elapsedMs={}",
                batch.size(), verified.size(), needCount, elapsedMs);
        return verified;
    }

    private String takeProxyProbeResult(CompletionService<String> completionService) {
        try {
            Future<String> future = completionService.take();
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private void waitForWarmup() {
        long deadline = System.currentTimeMillis() + Math.max(1_000, properties.getProxyPoolWarmupWaitMs());
        while (refreshing.get() && healthyProxies.isEmpty() && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private List<String> fetchCandidates(boolean force) {
        if (!force && !cacheExpired() && !healthyProxies.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> proxies = new LinkedHashSet<>();
        if (properties.getProxyPool() != null) {
            properties.getProxyPool().stream()
                    .filter(StringUtils::hasText)
                    .map(this::normalize)
                    .forEach(proxies::add);
        }
        List<String> providerUrls = providerUrls();
        int candidateLimit = Math.max(properties.getProxyCandidateLimit(), properties.getMaxProxyPoolSize());
        for (String configuredUrl : providerUrls) {
            for (String providerUrl : expandProviderUrls(configuredUrl)) {
                try {
                    String body = httpClient.getByUrlConnection(providerUrl);
                    proxies.addAll(parseProxies(body));
                    if (proxies.size() >= candidateLimit) {
                        break;
                    }
                } catch (Exception e) {
                    log.warn("同花顺代理API拉取失败, providerUrl={}, error={}", providerUrl, e.getMessage());
                }
            }
            if (proxies.size() >= candidateLimit) {
                break;
            }
        }
        lastProviderFetchTime = Instant.now();
        List<String> candidates = proxies.stream()
                .filter(this::isValidProxy)
                .limit(candidateLimit)
                .toList();
        candidateProxies = List.copyOf(candidates);
        return candidates;
    }

    private Set<String> parseProxies(String body) {
        Set<String> proxies = new LinkedHashSet<>();
        if (!StringUtils.hasText(body)) {
            return proxies;
        }
        proxies.addAll(parseJsonProxies(body));
        boolean html = body.contains("<");
        proxies.addAll(parseHtmlTableProxies(body));
        Matcher matcher = PROXY_PATTERN.matcher(body);
        while (matcher.find()) {
            proxies.add(normalize(matcher.group(1)));
        }
        Matcher columnMatcher = IP_PORT_COLUMNS_PATTERN.matcher(body);
        while (columnMatcher.find()) {
            proxies.add(normalize(columnMatcher.group(1) + ":" + columnMatcher.group(2)));
        }
        if (html) {
            String text = Jsoup.parse(body).text();
            matcher = PROXY_PATTERN.matcher(text);
            while (matcher.find()) {
                proxies.add(normalize(matcher.group(1)));
            }
            columnMatcher = IP_PORT_COLUMNS_PATTERN.matcher(text);
            while (columnMatcher.find()) {
                proxies.add(normalize(columnMatcher.group(1) + ":" + columnMatcher.group(2)));
            }
        }
        Set<String> validProxies = new LinkedHashSet<>();
        for (String proxy : proxies) {
            String normalized = normalize(proxy);
            if (isValidProxy(normalized)) {
                validProxies.add(normalized);
            }
        }
        return validProxies;
    }

    private Set<String> parseHtmlTableProxies(String body) {
        Set<String> proxies = new LinkedHashSet<>();
        if (!StringUtils.hasText(body) || !body.contains("<")) {
            return proxies;
        }
        Document document = Jsoup.parse(body);
        for (Element row : document.select("tr")) {
            List<String> cells = row.select("th,td").stream()
                    .map(Element::text)
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .toList();
            addCellProxy(cells, proxies);
        }
        return proxies;
    }

    private void addCellProxy(List<String> cells, Set<String> proxies) {
        String ip = "";
        String port = "";
        for (String cell : cells) {
            if (!StringUtils.hasText(ip) && cell.matches("(?:\\d{1,3}\\.){3}\\d{1,3}")) {
                ip = cell;
                continue;
            }
            if (!StringUtils.hasText(port) && cell.matches("\\d{2,5}")) {
                port = cell;
            }
        }
        if (StringUtils.hasText(ip) && StringUtils.hasText(port)) {
            proxies.add(normalize(ip + ":" + port));
        }
    }

    private Set<String> parseJsonProxies(String body) {
        Set<String> proxies = new LinkedHashSet<>();
        String trimmed = body.trim();
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            return proxies;
        }
        try {
            JsonNode root = objectMapper.readTree(trimmed);
            collectJsonProxy(root, proxies);
        } catch (Exception ignored) {
            // Some proxy APIs return pseudo-json or mixed text; regex parsing below still handles ip:port.
        }
        return proxies;
    }

    private void collectJsonProxy(JsonNode node, Set<String> proxies) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            addObjectProxy(node, proxies);
            node.fields().forEachRemaining(entry -> collectJsonProxy(entry.getValue(), proxies));
            return;
        }
        if (node.isArray()) {
            node.forEach(child -> collectJsonProxy(child, proxies));
        }
    }

    private void addObjectProxy(JsonNode node, Set<String> proxies) {
        String ip = firstText(node, "ip", "host", "proxy", "addr", "address");
        String port = firstText(node, "port");
        if (StringUtils.hasText(ip) && ip.contains(":")) {
            proxies.add(normalize(ip));
            return;
        }
        if (StringUtils.hasText(ip) && StringUtils.hasText(port)) {
            proxies.add(normalize(ip + ":" + port));
        }
    }

    private String firstText(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && value.isValueNode() && StringUtils.hasText(value.asText())) {
                return value.asText().trim();
            }
        }
        return "";
    }

    private boolean testProxy(String proxy) {
        try {
            int timeout = Math.max(1_000, properties.getProxyTestTimeoutMs());
            String body = httpClient.getByUrlConnection(
                    properties.getProxyTestUrl(), thsProbeHeaders(), proxy, timeout, timeout);
            if (!StringUtils.hasText(body)) {
                return false;
            }
            String lower = body.toLowerCase();
            for (String blockWord : BLOCK_WORDS) {
                if (lower.contains(blockWord.toLowerCase())) {
                    return false;
                }
            }
            return lower.contains("10jqka") || body.contains("同花顺") || body.contains("板块") || body.length() > 1000;
        } catch (Exception e) {
            log.debug("同花顺代理探测失败, proxy={}, error={}", proxy, e.getMessage());
            return false;
        }
    }

    private java.util.Map<String, String> thsProbeHeaders() {
        java.util.Map<String, String> headers = new java.util.LinkedHashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.put("Referer", "http://q.10jqka.com.cn/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                + "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        return headers;
    }

    private List<String> providerUrls() {
        Set<String> urls = new LinkedHashSet<>();
        if (StringUtils.hasText(properties.getProxyProviderUrl())) {
            urls.add(properties.getProxyProviderUrl().trim());
        }
        if (properties.getProxyProviderUrls() != null) {
            properties.getProxyProviderUrls().stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .forEach(urls::add);
        }
        urls.addAll(DEFAULT_PROVIDER_URLS);
        return new ArrayList<>(urls);
    }

    private List<String> expandProviderUrls(String providerUrl) {
        if (!StringUtils.hasText(providerUrl)) {
            return List.of();
        }
        String trimmed = providerUrl.trim();
        int pageLimit = Math.max(1, properties.getProxyProviderPageLimit());
        if (trimmed.contains("89ip.cn")) {
            List<String> urls = new ArrayList<>();
            urls.add(trimmed);
            String root = trimmed.replaceAll("/index_\\d+\\.html$", "").replaceAll("/$", "");
            for (int page = 2; page <= pageLimit; page++) {
                urls.add(root + "/index_" + page + ".html");
            }
            return urls;
        }
        if (!trimmed.contains("qiyunip.com/freeProxy")) {
            return List.of(trimmed);
        }
        List<String> urls = new ArrayList<>();
        String base = trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
        urls.add(base);
        for (int page = 2; page <= pageLimit; page++) {
            urls.add(base + "/" + page + ".html");
        }
        return urls;
    }

    private boolean cacheExpired() {
        long refreshSeconds = Math.max(30, properties.getProxyPoolRefreshSeconds());
        return Duration.between(lastProviderFetchTime, Instant.now()).getSeconds() >= refreshSeconds;
    }

    private boolean isValidProxy(String proxyAddress) {
        if (!StringUtils.hasText(proxyAddress) || !PROXY_PATTERN.matcher(proxyAddress).matches()) {
            return false;
        }
        String[] parts = proxyAddress.split(":", 2);
        if (!validIp(parts[0])) {
            return false;
        }
        try {
            int port = Integer.parseInt(parts[1]);
            return port > 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        int first;
        int second;
        try {
            first = Integer.parseInt(parts[0]);
            second = Integer.parseInt(parts[1]);
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return first != 0
                && first != 10
                && first != 127
                && !(first == 169 && second == 254)
                && !(first == 172 && second >= 16 && second <= 31)
                && !(first == 192 && second == 168);
    }

    private String normalize(String proxyAddress) {
        return proxyAddress
                .replace("http://", "")
                .replace("https://", "")
                .trim();
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
}
