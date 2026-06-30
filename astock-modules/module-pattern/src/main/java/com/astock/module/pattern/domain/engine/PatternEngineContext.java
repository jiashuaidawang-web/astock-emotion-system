package com.astock.module.pattern.domain.engine;

import java.time.LocalDate;
import lombok.Data;

/**
 * PatternEngineContext 数据载体。
 */
@Data
public class PatternEngineContext {
    /** 任务ID。 */
    private Long taskId;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** 规则版本ID。 */
    private Long ruleVersionId;
    /** forceRecalculate 字段。 */
    private Boolean forceRecalculate;
    /** dataCheckEnabled 字段。 */
    private Boolean dataCheckEnabled;
    /** futureLeakageCheckEnabled 字段。 */
    private Boolean futureLeakageCheckEnabled;
    /** 参数JSON。 */
    private String paramJson;
}
