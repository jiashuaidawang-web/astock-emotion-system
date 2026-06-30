package com.astock.module.sample.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * HistoricalCycleSamplePageQuery 数据载体。
 */
@Data
public class HistoricalCycleSamplePageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
