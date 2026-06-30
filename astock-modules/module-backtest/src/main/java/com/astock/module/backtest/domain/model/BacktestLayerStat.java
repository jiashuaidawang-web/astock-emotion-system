package com.astock.module.backtest.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * BacktestLayerStat 数据载体。
 */
@Data
public class BacktestLayerStat {
    /** layerCode 字段。 */
    private String layerCode;
    /** layerName 字段。 */
    private String layerName;
    /** sampleCount 字段。 */
    private Integer sampleCount;
    /** effectiveSignalCount 字段。 */
    private Integer effectiveSignalCount;
    /** riskVetoCount 字段。 */
    private Integer riskVetoCount;
    /** winRate 字段。 */
    private BigDecimal winRate;
    /** avgReturn 字段。 */
    private BigDecimal avgReturn;
    /** avgDrawdown 字段。 */
    private BigDecimal avgDrawdown;
    /** profitLossRatio 字段。 */
    private BigDecimal profitLossRatio;
    /** evidenceJson 字段。 */
    private String evidenceJson;
}
