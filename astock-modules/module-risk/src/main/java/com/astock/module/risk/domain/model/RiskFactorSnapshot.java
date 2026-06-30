package com.astock.module.risk.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * RiskFactorSnapshot 数据载体。
 */
@Data
public class RiskFactorSnapshot {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** emotionStage 字段。 */
    private String emotionStage;
    /** emotionStageConfidence 字段。 */
    private BigDecimal emotionStageConfidence;
    /** lossEffectScore 字段。 */
    private BigDecimal lossEffectScore;
    /** limitDownPressureScore 字段。 */
    private BigDecimal limitDownPressureScore;
    /** breakBoardPressureScore 字段。 */
    private BigDecimal breakBoardPressureScore;
    /** leaderNegativeFeedbackScore 字段。 */
    private BigDecimal leaderNegativeFeedbackScore;
    /** mainlineDecayScore 字段。 */
    private BigDecimal mainlineDecayScore;
    /** indexFundRiskScore 字段。 */
    private BigDecimal indexFundRiskScore;
    /** dataIntegrityRiskScore 字段。 */
    private BigDecimal dataIntegrityRiskScore;
    /** emotionRow 字段。 */
    private Map<String, Object> emotionRow;
    /** marketRow 字段。 */
    private Map<String, Object> marketRow;
    /** limitEcoRow 字段。 */
    private Map<String, Object> limitEcoRow;
}
