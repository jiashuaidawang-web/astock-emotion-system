package com.astock.module.spider.checkpoint;

import lombok.Getter;

@Getter
public enum SpiderCheckpointStatus {

    CREATED(0),
    RUNNING(1),
    COMPLETED(2),
    FAILED(3);

    private final int code;

    SpiderCheckpointStatus(int code) {
        this.code = code;
    }
}
