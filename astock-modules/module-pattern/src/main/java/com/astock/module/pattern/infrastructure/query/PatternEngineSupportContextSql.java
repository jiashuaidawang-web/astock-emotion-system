package com.astock.module.pattern.infrastructure.query;

public final class PatternEngineSupportContextSql {
    private PatternEngineSupportContextSql() {}

    public static final String SELECT_PATTERN_BACKTEST_RESULT =
            "select * from pattern_backtest_result where trade_date <= :tradeDate order by trade_date desc limit 500";

    public static final String SELECT_RISK_BINDING =
            "select * from pattern_risk_binding where is_deleted = 0 limit 500";

    public static final String SELECT_STAGE_MATRIX =
            "select * from buy_pattern_stage_matrix where is_deleted = 0 limit 500";

    public static final String SELECT_RULE_CONFIG =
            "select * from buy_pattern_rule_config where is_deleted = 0 limit 500";
}
