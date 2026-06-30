package com.astock.module.emotion.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * EmotionStageScore 数据载体。
 */
@Data
public class EmotionStageScore {
    /** stageType 字段。 */
    private EmotionStageType stageType;
    /** factorPercentileMatchScore 字段。 */
    private BigDecimal factorPercentileMatchScore;
    /** historicalSampleSimilarityScore 字段。 */
    private BigDecimal historicalSampleSimilarityScore;
    /** stagePathMatchScore 字段。 */
    private BigDecimal stagePathMatchScore;
    /** followingValidationScore 字段。 */
    private BigDecimal followingValidationScore;
    /** manualSampleCorrectionScore 字段。 */
    private BigDecimal manualSampleCorrectionScore;
    /** stageScore 字段。 */
    private BigDecimal stageScore;
    /** rankNo 字段。 */
    private Integer rankNo;
    /** evidenceText 字段。 */
    private String evidenceText;
    /** riskText 字段。 */
    private String riskText;
}
