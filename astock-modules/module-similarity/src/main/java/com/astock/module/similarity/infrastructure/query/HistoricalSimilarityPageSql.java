package com.astock.module.similarity.infrastructure.query;

public final class HistoricalSimilarityPageSql {
    private HistoricalSimilarityPageSql() {}

    public static final String CK_MARKET_FACTOR_SNAPSHOT = "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_EMOTION_STAGE_SNAPSHOT = "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_SIMILARITY_MATCH = "select * from historical_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_SIMILARITY_FACTOR_DETAIL = "select * from historical_similarity_factor_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_FOLLOWING_PERFORMANCE = "select * from historical_following_performance where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_CYCLE_SAMPLE = "select * from historical_cycle_sample where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_SIMILARITY_RULE_VERSION = "select * from similarity_rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_CYCLE_SAMPLE_CONFIRM = "select * from cycle_sample_confirm where trade_date = :tradeDate limit :limit";
}
