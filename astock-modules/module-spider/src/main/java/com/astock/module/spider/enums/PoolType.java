package com.astock.module.spider.enums;

import lombok.Getter;

@Getter
public enum PoolType {

    LIMIT_UP("LIMIT_UP", "涨停池"),
    YEST_LIMIT_UP("YEST_LIMIT_UP", "昨日涨停池"),
    LIMIT_DOWN("LIMIT_DOWN", "跌停池"),
    SUB_NEW("SUB_NEW", "次新股池"),
    STRONG("STRONG", "强势股池"),
    BROKEN("BROKEN", "炸板池");

    private final String code;
    private final String name;

    PoolType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
