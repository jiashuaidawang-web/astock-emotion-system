package com.astock.module.spider.enums;

import lombok.Getter;

@Getter
public enum CheckStatus {

    UNCHECKED(0),
    SUCCESS(1),
    COUNT_MISMATCH(2),
    SERIOUS_ERROR(3);

    private final int code;

    CheckStatus(int code) {
        this.code = code;
    }
}
