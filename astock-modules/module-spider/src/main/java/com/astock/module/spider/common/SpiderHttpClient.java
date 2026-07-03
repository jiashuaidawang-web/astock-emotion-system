package com.astock.module.spider.common;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class SpiderHttpClient {

    private final RestTemplate restTemplate;

    public SpiderHttpClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(20))
                .build();
    }

    public String get(String url) {
        RequestEntity<Void> request = RequestEntity.get(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36")
                .header(HttpHeaders.ACCEPT, "application/json,text/javascript,*/*;q=0.8")
                .build();
        return restTemplate.exchange(request, String.class).getBody();
    }

    public String get(String url, Map<String, String> headers) {
        RequestEntity.HeadersBuilder<?> builder = RequestEntity.get(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36")
                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return restTemplate.exchange(builder.build(), String.class).getBody();
    }
}
