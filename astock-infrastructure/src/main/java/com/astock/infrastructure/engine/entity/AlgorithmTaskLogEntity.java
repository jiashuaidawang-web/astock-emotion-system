package com.astock.infrastructure.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 算法任务执行日志实体。
 *
 * <p>对应 MySQL 表 algorithm_task_log，用于记录单个算法引擎任务的开始、结束、状态与输出摘要。</p>
 */
@Data
@TableName("algorithm_task_log")
public class AlgorithmTaskLogEntity {

    /** 算法任务ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 任务名称。 */
    private String taskName;

    /** 任务类型。 */
    private String taskType;

    /** 交易日。 */
    private LocalDate tradeDate;

    /** 任务状态。 */
    private String taskStatus;

    /** 开始时间。 */
    private LocalDateTime startedAt;

    /** 结束时间。 */
    private LocalDateTime finishedAt;

    /** 耗时毫秒。 */
    private Long costMillis;

    /** 输入参数JSON。 */
    private String inputJson;

    /** 输出JSON。 */
    private String outputJson;

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
