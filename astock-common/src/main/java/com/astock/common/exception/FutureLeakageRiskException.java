package com.astock.common.exception;

public class FutureLeakageRiskException extends BusinessException {
    public FutureLeakageRiskException(String message) {
        super("FUTURE_LEAKAGE_RISK", message);
    }
}
