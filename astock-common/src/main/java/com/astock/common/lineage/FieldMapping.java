package com.astock.common.lineage;

import lombok.Data;

/**
 * FieldMapping 数据载体。
 */
@Data
public class FieldMapping {
    /** voField 字段。 */
    private final String voField;
    /** 来源类型。 */
    private final String sourceType;
    /** 来源表。 */
    private final String sourceTable;
    /** 来源列。 */
    private final String sourceColumn;
    /** formula 字段。 */
    private final String formula;
    /** 是否必填。 */
    private final boolean required;

    public FieldMapping(String voField, String sourceType, String sourceTable, String sourceColumn, String formula, boolean required) {
        this.voField = voField;
        this.sourceType = sourceType;
        this.sourceTable = sourceTable;
        this.sourceColumn = sourceColumn;
        this.formula = formula;
        this.required = required;
    }
}
