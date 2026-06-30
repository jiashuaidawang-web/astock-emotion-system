package com.astock.app.orchestration;

import java.time.LocalDate;
import lombok.Data;

/**
 * EngineBatchRunRequest 数据载体。
 */
@Data
public class EngineBatchRunRequest {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** 规则版本ID。 */
    private Long ruleVersionId;
    /** dataCheckEnabled 字段。 */
    private Boolean dataCheckEnabled = true;
    /** continueOnFailure 字段。 */
    private Boolean continueOnFailure = false;
    /** 是否运行回测。 */
    private Boolean runBacktest = false;
    /** runAgentAudit 字段。 */
    private Boolean runAgentAudit = true;
    /** 模式判定后是否再次执行风控。 */
    private Boolean rerunRiskAfterPattern = true;
    /** 参数JSON。 */
    private String paramJson;
}
