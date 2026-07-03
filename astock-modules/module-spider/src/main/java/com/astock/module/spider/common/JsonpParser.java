package com.astock.module.spider.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class JsonpParser {

    private final ObjectMapper objectMapper;

    public JsonpParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode parse(String body) throws IOException {
        return objectMapper.readTree(strip(body));
    }

    public String strip(String body) {
        if (body == null) {
            return "{}";
        }
        String trimmed = body.trim();
        int start = trimmed.indexOf('(');
        int end = trimmed.lastIndexOf(')');
        if (start >= 0 && end > start) {
            return trimmed.substring(start + 1, end);
        }
        return trimmed;
    }
}
