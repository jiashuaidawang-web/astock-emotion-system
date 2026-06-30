package com.astock.module.emotion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * EmotionMarketFeature 数据载体。
 */
@Data
public class EmotionMarketFeature {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** marketBreadthScore 字段。 */
    private BigDecimal marketBreadthScore;
    /** profitEffectScore 字段。 */
    private BigDecimal profitEffectScore;
    /** lossEffectScore 字段。 */
    private BigDecimal lossEffectScore;
    /** limitUpEcoScore 字段。 */
    private BigDecimal limitUpEcoScore;
    /** limitDownPressureScore 字段。 */
    private BigDecimal limitDownPressureScore;
    /** ladderHeightScore 字段。 */
    private BigDecimal ladderHeightScore;
    /** breakBoardPressureScore 字段。 */
    private BigDecimal breakBoardPressureScore;
    /** turnoverHeatScore 字段。 */
    private BigDecimal turnoverHeatScore;
    /** marketRow 字段。 */
    private Map<String, Object> marketRow;
    /** limitEcoRow 字段。 */
    private Map<String, Object> limitEcoRow;
}
