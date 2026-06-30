package com.astock.module.sector.infrastructure.query;

public final class SectorStrengthPageSql {
    private SectorStrengthPageSql() {}

    public static final String CK_SECTOR_STRENGTH_SNAPSHOT = "select * from sector_strength_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_SECTOR_DAILY_SNAPSHOT = "select * from sector_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_SECTOR_STOCK_MAPPING_SNAPSHOT = "select * from sector_stock_mapping_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_THEME_STRENGTH_SNAPSHOT = "select * from theme_strength_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_DETAIL = "select * from risk_signal_detail where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_SECTOR_RULE_VERSION = "select * from sector_rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_THEME_DEFINITION = "select * from theme_definition where is_deleted = 0 order by id desc limit :limit";
}
