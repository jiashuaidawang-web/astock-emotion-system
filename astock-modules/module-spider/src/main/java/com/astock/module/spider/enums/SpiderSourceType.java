package com.astock.module.spider.enums;

import lombok.Getter;

@Getter
public enum SpiderSourceType {

    THS(0, "同花顺"),
    EAST_MONEY(1, "东方财富");

    private final int code;
    private final String name;

    SpiderSourceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
