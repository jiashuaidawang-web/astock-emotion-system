package com.astock.module.market.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * MarketDashboardQuery 数据载体。
 */
@Data
public class MarketDashboardQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
