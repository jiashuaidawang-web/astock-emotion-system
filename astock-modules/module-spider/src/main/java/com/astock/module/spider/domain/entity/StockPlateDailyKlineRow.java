package com.astock.module.spider.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class StockPlateDailyKlineRow {
    private LocalDate tradeDate;
    private Integer type;
    private Integer plateType;
    private String plateCode;
    private String plateName;
    private BigDecimal closePrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal preClosePrice;
    private BigDecimal pctChange;
    private BigDecimal changeAmount;
    private Long volume;
    private BigDecimal turnoverAmount;
    private BigDecimal amplitude;
    private BigDecimal volumeRatio;
    private BigDecimal turnoverRate;
    private BigDecimal totalMarketValue;
    private BigDecimal floatMarketValue;
    private String features;
}
