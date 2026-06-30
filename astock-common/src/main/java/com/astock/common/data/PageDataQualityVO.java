package com.astock.common.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * PageDataQualityVO 数据载体。
 */
@Data
public class PageDataQualityVO {
    /** 页面编码。 */
    private String pageCode;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** checks 字段。 */
    private List<DataQualityCheckVO> checks = new ArrayList<>();
}
