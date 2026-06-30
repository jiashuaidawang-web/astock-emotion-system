package com.astock.module.backtest.application.service;

import com.astock.module.backtest.api.vo.BacktestReportDetailVO;
import com.astock.module.backtest.application.query.BacktestReportDetailQuery;

public interface BacktestReportQueryService {
    BacktestReportDetailVO queryReportDetail(BacktestReportDetailQuery query);
}
