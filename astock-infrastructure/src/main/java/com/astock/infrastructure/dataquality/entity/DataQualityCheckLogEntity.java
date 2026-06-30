package com.astock.infrastructure.dataquality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 数据完整性检查日志实体。
 *
 * <p>对应 MySQL 表 data_quality_check_log，用于记录页面或算法任务依赖快照是否完整。</p>
 */
@Data
@TableName("data_quality_check_log")
public class DataQualityCheckLogEntity {

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 页面编码或任务编码。 */
    private String pageCode;

    /** 快照编码。 */
    private String snapshotCode;

    /** 快照表名。 */
    private String snapshotTable;

    /** 交易日。 */
    private LocalDate tradeDate;

    /** 市场范围。 */
    private String marketScope;

    /** 数据是否完整：1完整，0缺失。 */
    private Integer dataComplete;

    /** 完整率。 */
    private BigDecimal completenessRatio;

    /** 是否关键数据：1关键，0非关键。 */
    private Integer critical;

    /** 缺失原因。 */
    private String missingReason;

    /** 检查状态。 */
    private String checkStatus;

    /** 扩展字段JSON。 */
    private String features;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;

    /** 逻辑删除：0否，1是。 */
    private Integer isDeleted;
}
