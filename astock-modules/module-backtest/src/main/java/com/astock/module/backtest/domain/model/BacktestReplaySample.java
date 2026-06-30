package com.astock.module.backtest.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * BacktestReplaySample 数据载体。
 */
@Data
public class BacktestReplaySample {
    /** sampleId 字段。 */
    private Long sampleId;
    /** sampleDate 字段。 */
    private LocalDate sampleDate;
    /** 市场范围。 */
    private String marketScope;
    /** stageCode 字段。 */
    private String stageCode;
    /** patternCode 字段。 */
    private String patternCode;
    /** stockCode 字段。 */
    private String stockCode;
    /** stockName 字段。 */
    private String stockName;
    /** mainlineCode 字段。 */
    private String mainlineCode;
    /** mainlineName 字段。 */
    private String mainlineName;
    /** signalScore 字段。 */
    private BigDecimal signalScore;
    /** riskScore 字段。 */
    private BigDecimal riskScore;
    /** riskAction 字段。 */
    private String riskAction;
    /** future1dReturn 字段。 */
    private BigDecimal future1dReturn;
    /** future3dReturn 字段。 */
    private BigDecimal future3dReturn;
    /** future5dReturn 字段。 */
    private BigDecimal future5dReturn;
    /** future10dReturn 字段。 */
    private BigDecimal future10dReturn;
    /** maxDrawdown 字段。 */
    private BigDecimal maxDrawdown;
    /** sampleRow 字段。 */
    private Map<String, Object> sampleRow;
    /** signalRow 字段。 */
    private Map<String, Object> signalRow;
    /** riskRow 字段。 */
    private Map<String, Object> riskRow;
}
