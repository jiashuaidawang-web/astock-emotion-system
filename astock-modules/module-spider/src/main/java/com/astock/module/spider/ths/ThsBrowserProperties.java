package com.astock.module.spider.ths;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spider.ths.browser")
public class ThsBrowserProperties {

    /**
     * Selenium standalone/chrome remote endpoint. Example: http://selenium-chrome:4444/wd/hub
     */
    private String remoteUrl;

    /**
     * Local Chrome/Chromium binary path. Only used when remoteUrl is blank.
     */
    private String chromeBinary;

    /**
     * Local chromedriver path. Only used when remoteUrl is blank.
     */
    private String chromeDriver;

    private boolean headless = true;

    /**
     * Optional HTTP proxy pool, format: ip:port or http://ip:port.
     */
    private List<String> proxyPool = new ArrayList<>();

    /**
     * Optional proxy provider API. The response may be plain text or JSON as long as it contains ip:port values.
     */
    private String proxyProviderUrl;

    /**
     * Optional proxy provider APIs, separated by comma in environment variables.
     */
    private List<String> proxyProviderUrls = new ArrayList<>(List.of(
            "https://api.openproxylist.xyz/http.txt",
            "https://www.qiyunip.com/freeProxy",
            "https://www.89ip.cn/"
    ));

    /**
     * In-memory proxy cache refresh interval.
     */
    private long proxyPoolRefreshSeconds = 300;

    /**
     * Max proxy count kept from manual config and provider response.
     */
    private int maxProxyPoolSize = 200;

    /**
     * Lowest healthy proxy count kept in memory.
     */
    private int minUsableProxyCount = 30;

    /**
     * Max raw candidates parsed from provider APIs in one refresh round.
     */
    private int proxyCandidateLimit = 1000;

    /**
     * Max pages expanded for HTML proxy list providers, such as qiyunip.com/freeProxy.
     */
    private int proxyProviderPageLimit = 10;

    /**
     * Probe URL used to verify a proxy can really access TongHuaShun.
     */
    private String proxyTestUrl = "http://q.10jqka.com.cn/gn/";

    /**
     * Timeout for each proxy probe. Keep it short because public proxy lists contain many dead endpoints.
     */
    private int proxyTestTimeoutMs = 1_500;

    /**
     * Concurrent virtual-thread workers used to verify candidate proxies.
     */
    private int proxyTestConcurrency = 64;

    /**
     * Max candidates tested in one refresh round. Public proxy lists are noisy, so scanning all of them is too slow.
     */
    private int proxyTestCandidateLimit = 300;

    /**
     * Background proxy pool maintenance interval.
     */
    private long proxyPoolMonitorDelayMs = 60_000;

    /**
     * Delay before first background proxy pool maintenance.
     */
    private long proxyPoolInitialDelayMs = 15_000;

    /**
     * Print in-memory healthy proxy list periodically for production troubleshooting.
     */
    private boolean proxyPoolLogEnabled = true;

    /**
     * Interval for printing the in-memory healthy proxy list.
     */
    private long proxyPoolLogDelayMs = 5_000;

    /**
     * Max candidate proxies printed in periodic logs.
     */
    private int candidateProxyLogLimit = 200;

    /**
     * Retry count after anti-crawler responses. Each retry uses the next proxy.
     */
    private int maxProxyRetries = 30;

    /**
     * Try the server's own network first, then rotate to proxies only when blocked.
     */
    private boolean directFirst = false;

    /**
     * Max time a batch request waits when the proxy pool is being warmed up.
     */
    private long proxyPoolWarmupWaitMs = 60_000;

    private List<String> arguments = new ArrayList<>(List.of(
            "--disable-gpu",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080"
    ));
}
