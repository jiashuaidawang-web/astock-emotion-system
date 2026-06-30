package com.astock.module.backtest.domain.engine;

public interface BacktestExecutor {
    BacktestEngineResult execute(BacktestEngineContext context);
}
