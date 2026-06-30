package com.astock.module.backtest.domain.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * BacktestReplayResult 数据载体。
 */
@Data
public class BacktestReplayResult {
    /** sample 字段。 */
    private BacktestReplaySample sample;
    /** signalEffective 字段。 */
    private Boolean signalEffective;
    /** riskVetoed 字段。 */
    private Boolean riskVetoed;
    /** replayStatus 字段。 */
    private String replayStatus;
    /** failureType 字段。 */
    private String failureType;
    /** replayReturn 字段。 */
    private BigDecimal replayReturn;
    /** replayDrawdown 字段。 */
    private BigDecimal replayDrawdown;
    /** evidenceJson 字段。 */
    private String evidenceJson;
    /** riskJson 字段。 */
    private String riskJson;
}
