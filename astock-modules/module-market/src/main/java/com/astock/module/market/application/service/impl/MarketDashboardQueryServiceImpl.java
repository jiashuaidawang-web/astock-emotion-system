package com.astock.module.market.application.service.impl;

import com.astock.module.market.api.vo.MarketDashboardVO;
import com.astock.module.market.application.aggregator.MarketDashboardAggregator;
import com.astock.module.market.application.query.MarketDashboardQuery;
import com.astock.module.market.application.service.MarketDashboardQueryService;
import org.springframework.stereotype.Service;

@Service
public class MarketDashboardQueryServiceImpl implements MarketDashboardQueryService {

    private final MarketDashboardAggregator aggregator;

    public MarketDashboardQueryServiceImpl(MarketDashboardAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public MarketDashboardVO queryMarketDashboard(MarketDashboardQuery query) {
        return aggregator.aggregate(query);
    }
}
