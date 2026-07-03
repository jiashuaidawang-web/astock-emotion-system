package com.astock.module.spider.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class StockDailyKlineRow {
    private LocalDate tradeDate;
    private String stockCode;
    private String stockName;
    private String exchange;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal preClosePrice;
    private BigDecimal pctChange;
    private BigDecimal changeAmount;
    private Long volume;
    private BigDecimal turnoverAmount;
    private BigDecimal amplitude;
    private BigDecimal volumeRatio;
    private BigDecimal turnoverRate;
    private BigDecimal peDynamic;
    private BigDecimal pb;
    private BigDecimal totalMarketValue;
    private BigDecimal floatMarketValue;
    private BigDecimal featuresMainNetInflow;
    private String features;
}
