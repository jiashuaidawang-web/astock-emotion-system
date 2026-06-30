package com.astock.module.similarity.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * SimilarityDimensionScore 数据载体。
 */
@Data
public class SimilarityDimensionScore {
    /** dimensionType 字段。 */
    private SimilarityDimensionType dimensionType;
    /** currentValue 字段。 */
    private BigDecimal currentValue;
    /** historicalValue 字段。 */
    private BigDecimal historicalValue;
    /** similarityScore 字段。 */
    private BigDecimal similarityScore;
}
