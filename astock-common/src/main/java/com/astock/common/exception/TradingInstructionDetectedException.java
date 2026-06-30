package com.astock.common.exception;

public class TradingInstructionDetectedException extends BusinessException {
    public TradingInstructionDetectedException(String message) {
        super("TRADING_INSTRUCTION_DETECTED", message);
    }
}
