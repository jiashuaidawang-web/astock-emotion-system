package com.astock.module.pattern.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * PatternConditionPageQuery 数据载体。
 */
@Data
public class PatternConditionPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
