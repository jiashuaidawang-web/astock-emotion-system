package com.astock.module.leader.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * LeaderCandidateFeature 数据载体。
 */
@Data
public class LeaderCandidateFeature {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** stockCode 字段。 */
    private String stockCode;
    /** stockName 字段。 */
    private String stockName;
    /** sectorCode 字段。 */
    private String sectorCode;
    /** sectorName 字段。 */
    private String sectorName;
    /** mainlineCode 字段。 */
    private String mainlineCode;
    /** mainlineName 字段。 */
    private String mainlineName;
    /** pctChange 字段。 */
    private BigDecimal pctChange;
    /** turnoverAmount 字段。 */
    private BigDecimal turnoverAmount;
    /** turnoverRate 字段。 */
    private BigDecimal turnoverRate;
    /** volumeRatio 字段。 */
    private BigDecimal volumeRatio;
    /** boardHeight 字段。 */
    private BigDecimal boardHeight;
    /** maxBoardHeight 字段。 */
    private BigDecimal maxBoardHeight;
    /** limitUp 字段。 */
    private Boolean limitUp;
    /** brokenBoard 字段。 */
    private Boolean brokenBoard;
    /** mainlineStrengthScore 字段。 */
    private BigDecimal mainlineStrengthScore;
    /** sectorStrengthScore 字段。 */
    private BigDecimal sectorStrengthScore;
    /** negativeRawScore 字段。 */
    private BigDecimal negativeRawScore;
    /** sourceRow 字段。 */
    private Map<String, Object> sourceRow;
}
