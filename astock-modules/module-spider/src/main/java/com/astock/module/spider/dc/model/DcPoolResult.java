package com.astock.module.spider.dc.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.List;

public record DcPoolResult(int sourceTotalCount, LocalDate queryDate, List<JsonNode> rows) {
}
