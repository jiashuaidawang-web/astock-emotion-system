package com.astock.module.spider.enums;

import lombok.Getter;

@Getter
public enum PlateType {

    CONCEPT(0, "概念"),
    REGION(1, "地域"),
    CSRC_INDUSTRY(2, "证监会行业"),
    THS_INDUSTRY(3, "同花顺行业"),
    EAST_MONEY_INDUSTRY(2, "东方财富行业");

    private final int code;
    private final String name;

    PlateType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
