package com.astock.module.spider.dc.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record DcPageResult(int sourceTotalCount, List<JsonNode> rows) {
}
