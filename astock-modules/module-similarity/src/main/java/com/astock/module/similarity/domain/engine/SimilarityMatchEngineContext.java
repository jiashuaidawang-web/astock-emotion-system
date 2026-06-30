package com.astock.module.similarity.domain.engine;

import java.time.LocalDate;
import lombok.Data;

/**
 * SimilarityMatchEngineContext 数据载体。
 */
@Data
public class SimilarityMatchEngineContext {
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
