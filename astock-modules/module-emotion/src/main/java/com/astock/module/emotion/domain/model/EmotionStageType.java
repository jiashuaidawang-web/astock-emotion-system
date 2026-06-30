package com.astock.module.emotion.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

/**
 * EmotionStageType 枚举定义。
 */
@Getter
public enum EmotionStageType {
    ICE_POINT("ICE_POINT", "冰点",
            15, 10, 90, 10, 90, 5, 75, 20),
    REPAIR("REPAIR", "修复",
            35, 35, 65, 25, 55, 15, 45, 35),
    TRIAL("TRIAL", "试错",
            48, 45, 52, 38, 42, 25, 35, 45),
    STARTUP("STARTUP", "启动",
            58, 58, 40, 55, 30, 38, 25, 55),
    FERMENTATION("FERMENTATION", "发酵",
            68, 68, 28, 68, 20, 55, 20, 65),
    MAIN_RISE("MAIN_RISE", "主升",
            78, 78, 18, 80, 12, 70, 15, 75),
    CLIMAX("CLIMAX", "高潮",
            90, 90, 10, 95, 8, 85, 35, 95),
    DIVERGENCE("DIVERGENCE", "分歧",
            60, 55, 45, 55, 38, 60, 65, 70),
    RETREAT("RETREAT", "退潮",
            25, 15, 85, 15, 80, 20, 85, 35),
    CHAOS("CHAOS", "混沌",
            45, 35, 55, 30, 45, 30, 55, 45);

    /** 编码。 */
    private final String code;
    /** 名称。 */
    private final String name;
    /** 市场宽度中心值。 */
    private final double marketBreadthCenter;
    /** 赚钱效应中心值。 */
    private final double profitEffectCenter;
    /** 亏损效应中心值。 */
    private final double lossEffectCenter;
    /** 涨停生态中心值。 */
    private final double limitUpEcoCenter;
    /** 跌停压力中心值。 */
    private final double limitDownPressureCenter;
    /** 梯队高度中心值。 */
    private final double ladderHeightCenter;
    /** 断板压力中心值。 */
    private final double breakBoardPressureCenter;
    /** 成交热度中心值。 */
    private final double turnoverHeatCenter;

    EmotionStageType(String code,
                     String name,
                     double marketBreadthCenter,
                     double profitEffectCenter,
                     double lossEffectCenter,
                     double limitUpEcoCenter,
                     double limitDownPressureCenter,
                     double ladderHeightCenter,
                     double breakBoardPressureCenter,
                     double turnoverHeatCenter) {
        this.code = code;
        this.name = name;
        this.marketBreadthCenter = marketBreadthCenter;
        this.profitEffectCenter = profitEffectCenter;
        this.lossEffectCenter = lossEffectCenter;
        this.limitUpEcoCenter = limitUpEcoCenter;
        this.limitDownPressureCenter = limitDownPressureCenter;
        this.ladderHeightCenter = ladderHeightCenter;
        this.breakBoardPressureCenter = breakBoardPressureCenter;
        this.turnoverHeatCenter = turnoverHeatCenter;
    }

    public static List<EmotionStageType> orderedStages() {
        return Arrays.asList(values());
    }
}
