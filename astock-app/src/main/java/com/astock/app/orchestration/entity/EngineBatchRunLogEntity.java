package com.astock.app.orchestration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Engine 一键跑批批次日志实体。
 *
 * <p>对应 MySQL 表 engine_batch_run_log，用于记录一次全链路批处理的总体状态。</p>
 */
@Data
@TableName("engine_batch_run_log")
public class EngineBatchRunLogEntity {

    /** 批次ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 交易日。 */
    private LocalDate tradeDate;

    /** 市场范围。 */
    private String marketScope;

    /** 批次状态：RUNNING/SUCCESS/FAILED。 */
    private String batchStatus;

    /** 开始时间。 */
    private LocalDateTime startedAt;

    /** 结束时间。 */
    private LocalDateTime finishedAt;

    /** 耗时毫秒。 */
    private Long costMillis;

    /** 总步骤数。 */
    private Integer totalStepCount;

    /** 成功步骤数。 */
    private Integer successStepCount;

    /** 失败步骤数。 */
    private Integer failedStepCount;

    /** 请求JSON。 */
    private String requestJson;

    /** 失败原因。 */
    private String failureReason;

    /** 扩展字段JSON。 */
    private String features;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;

    /** 逻辑删除：0否，1是。 */
    private Integer isDeleted;
}
