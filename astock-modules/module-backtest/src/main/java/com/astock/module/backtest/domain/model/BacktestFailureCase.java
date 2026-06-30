package com.astock.module.backtest.domain.model;

import lombok.Data;

/**
 * BacktestFailureCase 数据载体。
 */
@Data
public class BacktestFailureCase {
    /** replayResult 字段。 */
    private BacktestReplayResult replayResult;
    /** failureType 字段。 */
    private String failureType;
    /** 失败原因。 */
    private String failureReason;
    /** evidenceJson 字段。 */
    private String evidenceJson;
}
