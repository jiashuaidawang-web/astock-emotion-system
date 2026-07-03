package com.astock.module.spider.ths;

import com.astock.module.spider.enums.PlateType;
import lombok.Getter;

@Getter
public enum ThsEndpoint {

    CONCEPT(PlateType.CONCEPT.getCode(), "概念", "http://q.10jqka.com.cn/gn/", "264648", true),
    REGION(PlateType.REGION.getCode(), "地域", "http://q.10jqka.com.cn/dy/", "199112", false),
    CSRC_INDUSTRY(PlateType.CSRC_INDUSTRY.getCode(), "证监会行业", "http://q.10jqka.com.cn/zjhhy/", "199112", false),
    THS_INDUSTRY(PlateType.THS_INDUSTRY.getCode(), "同花顺行业", "http://q.10jqka.com.cn/thshy/", "199112", false);

    private final int plateType;
    private final String plateTypeName;
    private final String url;
    private final String relationSortField;
    private final boolean needExpandAll;

    ThsEndpoint(int plateType, String plateTypeName, String url, String relationSortField, boolean needExpandAll) {
        this.plateType = plateType;
        this.plateTypeName = plateTypeName;
        this.url = url;
        this.relationSortField = relationSortField;
        this.needExpandAll = needExpandAll;
    }
}
