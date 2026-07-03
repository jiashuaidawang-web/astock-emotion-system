package com.astock.module.spider.dc.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record DcSinglePageResult(int sourceTotalCount, int totalPage, int pageNo, List<JsonNode> rows) {
}
