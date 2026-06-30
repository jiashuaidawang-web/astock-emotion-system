package com.astock.app.orchestration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * EngineBatchRunResult 数据载体。
 */
@Data
public class EngineBatchRunResult {
    /** 批次ID。 */
    private Long batchId;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** 是否成功。 */
    private Boolean success;
    /** 批次状态。 */
    private String batchStatus;
    /** 失败原因。 */
    private String failureReason;
    /** totalStepCount 字段。 */
    private Integer totalStepCount;
    /** 成功步骤数。 */
    private Integer successStepCount;
    /** failedStepCount 字段。 */
    private Integer failedStepCount;
    /** 开始时间。 */
    private LocalDateTime startedAt;
    /** 结束时间。 */
    private LocalDateTime finishedAt;
    /** 耗时毫秒。 */
    private Long costMillis;
    /** steps 字段。 */
    private List<EngineBatchStepResult> steps = new ArrayList<>();
}
