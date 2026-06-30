package com.astock.module.leader.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * LeaderScore 数据载体。
 */
@Data
public class LeaderScore {
    /** feature 字段。 */
    private LeaderCandidateFeature feature;
    /** recognitionScore 字段。 */
    private BigDecimal recognitionScore;
    /** mainlineRelationScore 字段。 */
    private BigDecimal mainlineRelationScore;
    /** driveScore 字段。 */
    private BigDecimal driveScore;
    /** strengthScore 字段。 */
    private BigDecimal strengthScore;
    /** supportScore 字段。 */
    private BigDecimal supportScore;
    /** continuityScore 字段。 */
    private BigDecimal continuityScore;
    /** riskFeedbackScore 字段。 */
    private BigDecimal riskFeedbackScore;
    /** negativeFeedbackScore 字段。 */
    private BigDecimal negativeFeedbackScore;
    /** leaderScore 字段。 */
    private BigDecimal leaderScore;
    /** rankNo 字段。 */
    private Integer rankNo;
    /** leaderType 字段。 */
    private String leaderType;
    /** leaderStatus 字段。 */
    private String leaderStatus;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskJson 字段。 */
    private String riskJson;
}
