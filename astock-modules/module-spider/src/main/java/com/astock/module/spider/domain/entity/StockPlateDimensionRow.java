package com.astock.module.spider.domain.entity;

import lombok.Data;

@Data
public class StockPlateDimensionRow {
    private Integer type;
    private Integer plateType;
    private String plateCode;
    private String plateName;
    private String features;
}
