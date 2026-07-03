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

    private List<String> arguments = new ArrayList<>(List.of(
            "--disable-gpu",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080"
    ));
}
