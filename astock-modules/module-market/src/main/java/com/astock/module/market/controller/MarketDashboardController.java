package com.astock.module.market.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.market.api.vo.MarketDashboardVO;
import com.astock.module.market.application.query.MarketDashboardQuery;
import com.astock.module.market.application.service.MarketDashboardQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class MarketDashboardController {

    private final MarketDashboardQueryService queryService;

    public MarketDashboardController(MarketDashboardQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/market")
    public ApiResult<MarketDashboardVO> market(MarketDashboardQuery query) {
        return ApiResult.success(queryService.queryMarketDashboard(query));
    }
}
