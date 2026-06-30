package com.astock.module.mainline.infrastructure.query;

public final class MainlineRadarPageSql {
    private MainlineRadarPageSql() {}

    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_THEME_STRENGTH_SNAPSHOT = "select * from theme_strength_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_THEME_DAILY_SNAPSHOT = "select * from theme_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_SWITCH_SNAPSHOT = "select * from mainline_switch_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DRIVE_SNAPSHOT = "select * from leader_drive_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_DETAIL = "select * from risk_signal_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_SIMILARITY_MATCH = "select * from mainline_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_THEME_DEFINITION = "select * from theme_definition where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_THEME_STOCK_MAPPING = "select * from theme_stock_mapping where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_MAINLINE_RULE_VERSION = "select * from mainline_rule_version where is_deleted = 0 order by id desc limit :limit";
}
