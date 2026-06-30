package com.astock.common.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * DataQualityCheckVO 数据载体。
 */
@Data
public class DataQualityCheckVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** dataDomain 字段。 */
    private String dataDomain;
    /** dataDomainName 字段。 */
    private String dataDomainName;
    /** 检查状态。 */
    private String checkStatus;
    /** 是否关键数据。 */
    private Boolean critical;
    /** expectedCount 字段。 */
    private Integer expectedCount;
    /** actualCount 字段。 */
    private Integer actualCount;
    /** missingCount 字段。 */
    private Integer missingCount;
    /** 完整率。 */
    private BigDecimal completenessRatio;
    /** impactPage 字段。 */
    private Boolean impactPage;
    /** checkText 字段。 */
    private String checkText;
}
