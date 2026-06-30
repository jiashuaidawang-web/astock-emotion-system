package com.astock.module.risk.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * RiskSignalScore 数据载体。
 */
@Data
public class RiskSignalScore {
    /** riskCode 字段。 */
    private String riskCode;
    /** riskName 字段。 */
    private String riskName;
    /** riskSource 字段。 */
    private String riskSource;
    /** signalLevel 字段。 */
    private String signalLevel;
    /** riskLevel 字段。 */
    private String riskLevel;
    /** riskScore 字段。 */
    private BigDecimal riskScore;
    /** riskAction 字段。 */
    private String riskAction;
    /** oneVoteVeto 字段。 */
    private Boolean oneVoteVeto;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskText 字段。 */
    private String riskText;
}
