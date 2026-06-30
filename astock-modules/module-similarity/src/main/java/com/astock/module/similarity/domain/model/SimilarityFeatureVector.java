package com.astock.module.similarity.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import lombok.Data;

/**
 * SimilarityFeatureVector 数据载体。
 */
@Data
public class SimilarityFeatureVector {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** stageCode 字段。 */
    private String stageCode;
    /** sampleId 字段。 */
    private Long sampleId;
    /** dimensions 字段。 */
    private final EnumMap<SimilarityDimensionType, BigDecimal> dimensions = new EnumMap<>(SimilarityDimensionType.class);

    public void put(SimilarityDimensionType dimension, BigDecimal value) {
        dimensions.put(dimension, value);
    }

    public BigDecimal get(SimilarityDimensionType dimension) {
        return dimensions.getOrDefault(dimension, BigDecimal.ZERO);
    }
}
