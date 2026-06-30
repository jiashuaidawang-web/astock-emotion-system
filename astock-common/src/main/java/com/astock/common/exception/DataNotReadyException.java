package com.astock.common.exception;

public class DataNotReadyException extends BusinessException {
    public DataNotReadyException(String message) {
        super("DATA_NOT_READY", message);
    }
}
