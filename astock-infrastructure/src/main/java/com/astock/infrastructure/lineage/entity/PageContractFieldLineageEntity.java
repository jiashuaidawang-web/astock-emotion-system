package com.astock.infrastructure.lineage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 页面字段血缘契约实体。
 *
 * <p>对应 MySQL 表 page_contract_field_lineage，用于描述前端 VO 字段与数据库/公式来源的映射关系。</p>
 */
@Data
@TableName("page_contract_field_lineage")
public class PageContractFieldLineageEntity {

    /** 主键ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 页面编码。 */
    private String pageCode;

    /** VO 类名。 */
    private String voClassName;

    /** 字段名。 */
    private String fieldName;

    /** 来源类型：MYSQL/CLICKHOUSE/FORMULA/CONSTANT。 */
    private String sourceType;

    /** 来源表。 */
    private String sourceTable;

    /** 来源列。 */
    private String sourceColumn;

    /** 计算公式。 */
    private String calculationFormula;

    /** 是否必填：1必填，0非必填。 */
    private Integer required;

    /** 审计是否通过：1通过，0未通过。 */
    private Integer auditPassed;

    /** 备注。 */
    private String remark;

    /** 扩展字段JSON。 */
    private String features;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;

    /** 逻辑删除：0否，1是。 */
    private Integer isDeleted;
}
