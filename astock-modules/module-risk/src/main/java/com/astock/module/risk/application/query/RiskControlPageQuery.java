package com.astock.module.risk.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * RiskControlPageQuery 数据载体。
 */
@Data
public class RiskControlPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
