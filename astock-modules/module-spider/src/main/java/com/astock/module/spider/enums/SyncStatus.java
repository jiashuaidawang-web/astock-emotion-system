package com.astock.module.spider.enums;

import lombok.Getter;

@Getter
public enum SyncStatus {

    CREATED(0),
    FETCHING(1),
    WRITING_CLICKHOUSE(2),
    SUCCESS(3),
    FAILED(4);

    private final int code;

    SyncStatus(int code) {
        this.code = code;
    }
}
