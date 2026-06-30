package com.astock.module.pattern.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * PatternSignalScore 数据载体。
 */
@Data
public class PatternSignalScore {
    /** patternType 字段。 */
    private PatternType patternType;
    /** watchObject 字段。 */
    private PatternWatchObject watchObject;
    /** cycleAdmissionScore 字段。 */
    private BigDecimal cycleAdmissionScore;
    /** mainlineValidScore 字段。 */
    private BigDecimal mainlineValidScore;
    /** leaderPositionScore 字段。 */
    private BigDecimal leaderPositionScore;
    /** triggerScore 字段。 */
    private BigDecimal triggerScore;
    /** backtestSupportScore 字段。 */
    private BigDecimal backtestSupportScore;
    /** manualCorrectionScore 字段。 */
    private BigDecimal manualCorrectionScore;
    /** patternConditionScore 字段。 */
    private BigDecimal patternConditionScore;
    /** riskVeto 字段。 */
    private Boolean riskVeto;
    /** riskVetoReason 字段。 */
    private String riskVetoReason;
    /** invalidated 字段。 */
    private Boolean invalidated;
    /** invalidatedReason 字段。 */
    private String invalidatedReason;
    /** conditionStatus 字段。 */
    private String conditionStatus;
    /** allowConditionMetDisplay 字段。 */
    private Boolean allowConditionMetDisplay;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskJson 字段。 */
    private String riskJson;
}
