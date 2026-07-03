package com.astock.module.spider.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("spider_task_checkpoint")
public class SpiderTaskCheckpointEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate tradeDate;
    private Integer sourceType;
    private String taskCode;
    private String bizKey;
    private Integer currentPage;
    private Integer totalPage;
    private Integer sourceTotalCount;
    private Integer fetchedCount;
    private Integer insertedCount;
    private Integer status;
    private String errorMessage;
    private String features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
