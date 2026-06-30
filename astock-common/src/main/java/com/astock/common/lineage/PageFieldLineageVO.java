package com.astock.common.lineage;

import lombok.Data;

/**
 * PageFieldLineageVO 数据载体。
 */
@Data
public class PageFieldLineageVO {
    /** 页面编码。 */
    private String pageCode;
    /** 页面名称。 */
    private String pageName;
    /** VO类名。 */
    private String voClassName;
    /** 字段名。 */
    private String fieldName;
    /** 字段说明。 */
    private String fieldComment;
    /** 来源类型。 */
    private String sourceType;
    /** 来源表。 */
    private String sourceTable;
    /** 来源列。 */
    private String sourceColumn;
    /** 计算公式。 */
    private String calculationFormula;
    /** 是否必填。 */
    private Boolean required;
    /** 审计是否通过。 */
    private Boolean auditPassed;
}
