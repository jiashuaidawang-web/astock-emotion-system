package com.astock.app.orchestration;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * EngineBatchStepResult 数据载体。
 */
@Data
public class EngineBatchStepResult {
    /** 步骤序号。 */
    private Integer stepNo;
    /** 步骤编码。 */
    private String stepCode;
    /** 步骤名称。 */
    private String stepName;
    /** 引擎名称。 */
    private String engineName;
    /** 是否成功。 */
    private Boolean success;
    /** 任务ID。 */
    private Long taskId;
    /** 输出行数。 */
    private Integer outputRowCount;
    /** 输出表列表。 */
    private List<String> outputTables;
    /** 失败原因。 */
    private String failureReason;
    /** 摘要文本。 */
    private String summaryText;
    /** 开始时间。 */
    private LocalDateTime startedAt;
    /** 结束时间。 */
    private LocalDateTime finishedAt;
    /** 耗时毫秒。 */
    private Long costMillis;
}
