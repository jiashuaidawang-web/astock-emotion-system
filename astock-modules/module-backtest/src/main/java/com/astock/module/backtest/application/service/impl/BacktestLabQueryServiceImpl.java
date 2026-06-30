package com.astock.module.backtest.application.service.impl;

import com.astock.module.backtest.api.vo.BacktestLabPageVO;
import com.astock.module.backtest.application.aggregator.BacktestLabAggregator;
import com.astock.module.backtest.application.query.BacktestLabPageQuery;
import com.astock.module.backtest.application.service.BacktestLabQueryService;
import org.springframework.stereotype.Service;

@Service
public class BacktestLabQueryServiceImpl implements BacktestLabQueryService {

    private final BacktestLabAggregator aggregator;

    public BacktestLabQueryServiceImpl(BacktestLabAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public BacktestLabPageVO queryLab(BacktestLabPageQuery query) {
        return aggregator.aggregate(query);
    }
}
