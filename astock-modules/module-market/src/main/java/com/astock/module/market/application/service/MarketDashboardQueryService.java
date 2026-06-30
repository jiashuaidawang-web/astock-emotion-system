package com.astock.module.market.application.service;

import com.astock.module.market.api.vo.MarketDashboardVO;
import com.astock.module.market.application.query.MarketDashboardQuery;

public interface MarketDashboardQueryService {
    MarketDashboardVO queryMarketDashboard(MarketDashboardQuery query);
}
