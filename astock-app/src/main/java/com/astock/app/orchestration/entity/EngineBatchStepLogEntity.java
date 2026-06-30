package com.astock.app.orchestration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Engine 一键跑批步骤日志实体。
 *
 * <p>对应 MySQL 表 engine_batch_step_log，用于记录批次中单个引擎步骤的执行状态。</p>
 */
@Data
@TableName("engine_batch_step_log")
public class EngineBatchStepLogEntity {

    /** 步骤日志ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 批次ID。 */
    private Long batchId;

    /** 步骤序号。 */
    private Integer stepNo;

    /** 步骤编码。 */
    private String stepCode;

    /** 步骤名称。 */
    private String stepName;

    /** 引擎名称。 */
    private String engineName;

    /** 步骤状态：SUCCESS/FAILED。 */
    private String stepStatus;

    /** 底层算法任务ID。 */
    private Long taskId;

    /** 输出行数。 */
    private Integer outputRowCount;

    /** 输出表列表。 */
    private String outputTables;

    /** 失败原因。 */
    private String failureReason;

    /** 摘要。 */
    private String summaryText;

    /** 开始时间。 */
    private LocalDateTime startedAt;

    /** 结束时间。 */
    private LocalDateTime finishedAt;

    /** 耗时毫秒。 */
    private Long costMillis;

    /** 扩展字段JSON。 */
    private String features;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;

    /** 逻辑删除：0否，1是。 */
    private Integer isDeleted;
}
