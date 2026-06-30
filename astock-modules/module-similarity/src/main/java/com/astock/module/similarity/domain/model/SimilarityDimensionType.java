package com.astock.module.similarity.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

/**
 * SimilarityDimensionType 枚举定义。
 */
@Getter
public enum SimilarityDimensionType {
    MARKET_BREADTH("MARKET_BREADTH", "市场宽度相似", "MARKET_ENV", 10),
    TURNOVER_VOLUME("TURNOVER_VOLUME", "成交额/量能相似", "MARKET_ENV", 10),
    INDEX_POSITION("INDEX_POSITION", "指数位置相似", "MARKET_ENV", 10),
    LIMIT_ECOLOGY("LIMIT_ECOLOGY", "涨跌停生态相似", "EMOTION_CYCLE", 15),
    LEADER_LADDER("LEADER_LADDER", "连板梯队相似", "EMOTION_CYCLE", 10),
    LOSS_EFFECT("LOSS_EFFECT", "亏钱效应相似", "EMOTION_CYCLE", 10),
    STAGE_PATH("STAGE_PATH", "阶段演化路径相似", "EMOTION_CYCLE", 10),
    MAINLINE_STRUCTURE("MAINLINE_STRUCTURE", "主线结构相似", "THEME_LEADER", 13),
    LEADER_FEEDBACK("LEADER_FEEDBACK", "龙头反馈相似", "THEME_LEADER", 12);

    /** 编码。 */
    private final String code;
    /** 名称。 */
    private final String name;
    /** 分组编码。 */
    private final String groupCode;
    /** 权重。 */
    private final int weight;

    SimilarityDimensionType(String code, String name, String groupCode, int weight) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.weight = weight;
    }

    public static List<SimilarityDimensionType> orderedDimensions() {
        return Arrays.asList(values());
    }
}
