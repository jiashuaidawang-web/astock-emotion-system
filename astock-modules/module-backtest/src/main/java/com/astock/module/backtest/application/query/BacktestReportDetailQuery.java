package com.astock.module.backtest.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * BacktestReportDetailQuery 数据载体。
 */
@Data
public class BacktestReportDetailQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** reportId 字段。 */
    private Long reportId;
    /** includeLayerStats 字段。 */
    private Boolean includeLayerStats;
    /** includeFailureCases 字段。 */
    private Boolean includeFailureCases;
}
