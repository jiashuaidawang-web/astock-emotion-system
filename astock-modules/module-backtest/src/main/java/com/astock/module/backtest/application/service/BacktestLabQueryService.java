package com.astock.module.backtest.application.service;

import com.astock.module.backtest.api.vo.BacktestLabPageVO;
import com.astock.module.backtest.application.query.BacktestLabPageQuery;

public interface BacktestLabQueryService {
    BacktestLabPageVO queryLab(BacktestLabPageQuery query);
}
