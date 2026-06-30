package com.astock.module.pattern.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

/**
 * PatternType 枚举定义。
 */
@Getter
public enum PatternType {
    ICE_REPAIR("ICE_REPAIR", "冰点修复观察条件"),
    MAINLINE_STARTUP("MAINLINE_STARTUP", "主线启动确认条件"),
    LEADER_DIVERGENCE_TO_CONSISTENCY("LEADER_DIVERGENCE_TO_CONSISTENCY", "龙头分歧转一致条件"),
    TREND_LEADER_PULLBACK("TREND_LEADER_PULLBACK", "趋势龙头回踩确认条件"),
    CLIMAX_NO_CHASE("CLIMAX_NO_CHASE", "高潮风险禁止追高条件"),
    RETREAT_STOP("RETREAT_STOP", "退潮停手机制");

    /** 编码。 */
    private final String code;
    /** 名称。 */
    private final String name;

    PatternType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static List<PatternType> orderedPatterns() {
        return Arrays.asList(values());
    }
}
