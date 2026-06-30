package com.astock.infrastructure.rule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 规则版本实体。
 *
 * <p>对应 MySQL 表 rule_version，用于承载算法引擎执行时必须绑定的规则版本。</p>
 */
@Data
@TableName("rule_version")
public class RuleVersionEntity {

    /** 规则版本ID。 */
    @TableId(type = IdType.AUTO)
    /** 主键ID。 */
    private Long id;

    /** 规则编码。 */
    private String ruleCode;

    /** 规则名称。 */
    private String ruleName;

    /** 版本号。 */
    private String versionNo;

    /** 版本名称。 */
    private String versionName;

    /** 版本状态：DRAFT/ACTIVE/ARCHIVED。 */
    private String versionStatus;

    /** 是否启用版本：1启用，0未启用。 */
    private Integer activeFlag;

    /** 规则内容JSON。 */
    private String ruleContentJson;

    /** 参数结构JSON。 */
    private String paramSchemaJson;

    /** 发布检查JSON。 */
    private String publishCheckJson;

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
