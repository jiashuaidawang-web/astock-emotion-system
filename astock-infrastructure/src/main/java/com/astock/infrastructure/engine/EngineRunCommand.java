package com.astock.infrastructure.engine;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * EngineRunCommand 数据载体。
 */
@Data
public class EngineRunCommand {
    /** 任务ID。 */
    private Long taskId;
    /** 任务名称。 */
    private String taskName;
    /** 任务类型。 */
    private String taskType;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** 规则编码。 */
    private String ruleCode;
    /** 规则版本ID。 */
    private Long ruleVersionId;
    /** 参数JSON。 */
    private String paramJson;
    /** inputTables 字段。 */
    private List<String> inputTables;
    /** 输出表列表。 */
    private List<String> outputTables;
    /** dataCheckEnabled 字段。 */
    private boolean dataCheckEnabled = true;
    /** failIfNoOutputRows 字段。 */
    private boolean failIfNoOutputRows = false;
}
