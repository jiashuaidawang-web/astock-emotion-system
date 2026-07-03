package com.astock.module.spider.domain.entity;

import java.time.LocalDate;
import lombok.Data;

@Data
public class StockPlateRelationRow {
    private LocalDate tradeDate;
    private Integer type;
    private Integer plateType;
    private String plateCode;
    private String plateName;
    private String stockCode;
    private String stockName;
    private String exchange;
    private String features;
}
