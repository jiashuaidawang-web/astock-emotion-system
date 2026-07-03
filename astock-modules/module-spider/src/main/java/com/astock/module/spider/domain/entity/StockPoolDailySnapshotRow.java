package com.astock.module.spider.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class StockPoolDailySnapshotRow {
    private LocalDate tradeDate;
    private String poolType;
    private String stockCode;
    private String stockName;
    private String exchange;
    private String industryName;
    private BigDecimal closePrice;
    private BigDecimal pctChange;
    private BigDecimal turnoverRate;
    private BigDecimal totalMarketValue;
    private BigDecimal floatMarketValue;
    private Integer isLimitUp;
    private Integer isBrokenBoard;
    private Integer boardHeight;
    private String limitUpTime;
    private String lastLimitUpTime;
    private Integer openImagesCount;
    private Integer daysSinceListed;
    private String features;
}
