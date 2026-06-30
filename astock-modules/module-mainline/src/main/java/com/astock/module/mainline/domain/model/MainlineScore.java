package com.astock.module.mainline.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * MainlineScore 数据载体。
 */
@Data
public class MainlineScore {
    /** feature 字段。 */
    private MainlineThemeFeature feature;
    /** limitUpClusterScore 字段。 */
    private BigDecimal limitUpClusterScore;
    /** turnoverConcentrationScore 字段。 */
    private BigDecimal turnoverConcentrationScore;
    /** continuityScore 字段。 */
    private BigDecimal continuityScore;
    /** ladderIntegrityScore 字段。 */
    private BigDecimal ladderIntegrityScore;
    /** leaderDriveScore 字段。 */
    private BigDecimal leaderDriveScore;
    /** emotionMatchScore 字段。 */
    private BigDecimal emotionMatchScore;
    /** mainlineStrengthScore 字段。 */
    private BigDecimal mainlineStrengthScore;
    /** rankNo 字段。 */
    private Integer rankNo;
    /** lifecycleStage 字段。 */
    private String lifecycleStage;
    /** mainlineStatus 字段。 */
    private String mainlineStatus;
    /** themeRole 字段。 */
    private String themeRole;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskJson 字段。 */
    private String riskJson;
}
