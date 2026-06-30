package com.astock.module.backtest.domain.engine;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * BacktestEngineResult 数据载体。
 */
@Data
public class BacktestEngineResult {
    /** 是否成功。 */
    private Boolean success;
    /** 任务ID。 */
    private Long taskId;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 输出表列表。 */
    private List<String> outputTables;
    /** 输出行数。 */
    private Integer outputRowCount;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** 未来函数检查是否通过。 */
    private Boolean futureLeakageCheckPassed;
    /** 失败原因。 */
    private String failureReason;
    /** 摘要文本。 */
    private String summaryText;
}
