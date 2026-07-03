package com.astock.module.spider.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("data_sync_audit_log")
public class DataSyncAuditLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate tradeDate;
    private LocalDate runDate;
    private String targetTable;
    private String syncType;
    private Integer sourceTotalCount;
    private Integer fetchedCount;
    private Integer insertedCount;
    private Integer checkStatus;
    private Integer syncStatus;
    private String errorMessage;
    private Integer retryCount;
    private String features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
