package com.astock.module.pattern.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * PatternWatchObject 数据载体。
 */
@Data
public class PatternWatchObject {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** stockCode 字段。 */
    private String stockCode;
    /** stockName 字段。 */
    private String stockName;
    /** leaderType 字段。 */
    private String leaderType;
    /** leaderStatus 字段。 */
    private String leaderStatus;
    /** watchObjectType 字段。 */
    private String watchObjectType;
    /** mainlineCode 字段。 */
    private String mainlineCode;
    /** mainlineName 字段。 */
    private String mainlineName;
    /** emotionStage 字段。 */
    private String emotionStage;
    /** leaderScore 字段。 */
    private BigDecimal leaderScore;
    /** mainlineStrengthScore 字段。 */
    private BigDecimal mainlineStrengthScore;
    /** riskScore 字段。 */
    private BigDecimal riskScore;
    /** riskLevel 字段。 */
    private String riskLevel;
    /** riskVeto 字段。 */
    private Boolean riskVeto;
    /** riskVetoReason 字段。 */
    private String riskVetoReason;
    /** patternCalculationAllowed 字段。 */
    private Boolean patternCalculationAllowed;
    /** leaderRow 字段。 */
    private Map<String, Object> leaderRow;
    /** mainlineRow 字段。 */
    private Map<String, Object> mainlineRow;
}
