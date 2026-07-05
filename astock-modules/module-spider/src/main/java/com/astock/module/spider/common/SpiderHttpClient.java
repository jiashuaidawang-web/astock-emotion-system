package com.astock.module.spider.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

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
        return get(url, headers, null);
    }

    public String get(String url, Map<String, String> headers, String proxyAddress) {
        RequestEntity.HeadersBuilder<?> builder = RequestEntity.get(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36")
                .header(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        if (headers != null) {
            headers.forEach(builder::header);
        }
        RestTemplate template = StringUtils.hasText(proxyAddress) ? proxiedRestTemplate(proxyAddress) : restTemplate;
        return template.exchange(builder.build(), String.class).getBody();
    }

    public String getByUrlConnection(String url) {
        return getByUrlConnection(url, null);
    }

    public String getByUrlConnection(String url, Map<String, String> headers) {
        return getByUrlConnection(url, headers, null);
    }

    public String getByUrlConnection(String url, Map<String, String> headers, String proxyAddress) {
        return getByUrlConnection(url, headers, proxyAddress, 10_000, 20_000);
    }

    public String getByUrlConnection(
            String url,
            Map<String, String> headers,
            String proxyAddress,
            int connectTimeoutMillis,
            int readTimeoutMillis) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL target = new URL(url);
            connection = StringUtils.hasText(proxyAddress)
                    ? (HttpURLConnection) target.openConnection(proxy(proxyAddress))
                    : (HttpURLConnection) target.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(Math.max(1_000, connectTimeoutMillis));
            connection.setReadTimeout(Math.max(1_000, readTimeoutMillis));
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }
            int statusCode = connection.getResponseCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException("HTTP GET failed, status=" + statusCode + ", url=" + url);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            return response.toString();
        } catch (IOException e) {
            throw new IllegalStateException("HTTP GET failed, url=" + url, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private Proxy proxy(String proxyAddress) {
        String normalized = proxyAddress
                .replace("http://", "")
                .replace("https://", "")
                .trim();
        String[] parts = normalized.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid proxy address: " + proxyAddress);
        }
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
    }

    private RestTemplate proxiedRestTemplate(String proxyAddress) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy(proxyAddress));
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(20_000);
        return new RestTemplate(factory);
    }
}
