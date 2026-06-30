package com.astock.module.similarity.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * SimilarityMatchCandidate 数据载体。
 */
@Data
public class SimilarityMatchCandidate {
    /** sampleId 字段。 */
    private Long sampleId;
    /** matchType 字段。 */
    private String matchType;
    /** historicalTradeDate 字段。 */
    private LocalDate historicalTradeDate;
    /** historicalStage 字段。 */
    private String historicalStage;
    /** marketEnvironmentSimilarityScore 字段。 */
    private BigDecimal marketEnvironmentSimilarityScore;
    /** emotionCycleSimilarityScore 字段。 */
    private BigDecimal emotionCycleSimilarityScore;
    /** themeLeaderSimilarityScore 字段。 */
    private BigDecimal themeLeaderSimilarityScore;
    /** totalSimilarityScore 字段。 */
    private BigDecimal totalSimilarityScore;
    /** dimensionScores 字段。 */
    private List<SimilarityDimensionScore> dimensionScores;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskText 字段。 */
    private String riskText;
}
