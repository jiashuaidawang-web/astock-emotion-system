package com.astock.module.rule.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * RuleVersionManagePageQuery 数据载体。
 */
@Data
public class RuleVersionManagePageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
