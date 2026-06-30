package com.astock.common.data;

import lombok.Data;

/**
 * RequiredSnapshot 数据载体。
 */
@Data
public class RequiredSnapshot {
    /** dataDomain 字段。 */
    private final String dataDomain;
    /** tableName 字段。 */
    private final String tableName;
    /** 是否关键数据。 */
    private final boolean critical;

    public RequiredSnapshot(String dataDomain, String tableName, boolean critical) {
        this.dataDomain = dataDomain;
        this.tableName = tableName;
        this.critical = critical;
    }
}
