package com.astock.module.leader.infrastructure.query;

public final class LeaderLadderPageSql {
    private LeaderLadderPageSql() {}

    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_LADDER_SNAPSHOT = "select * from leader_ladder_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DRIVE_SNAPSHOT = "select * from leader_drive_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_NEGATIVE_FEEDBACK = "select * from leader_negative_feedback where trade_date = :tradeDate limit :limit";
    public static final String CK_TREND_LEADER_SNAPSHOT = "select * from trend_leader_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_BUY_PATTERN_SIGNAL_SNAPSHOT = "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_LEADER_RULE_VERSION = "select * from leader_rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_LEADER_TYPE_DEFINITION = "select * from leader_type_definition where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_LEADER_MANUAL_CONFIRM = "select * from leader_manual_confirm where is_deleted = 0 order by id desc limit :limit";
}
