package com.astock.module.backtest.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * BacktestLabPageQuery 数据载体。
 */
@Data
public class BacktestLabPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
