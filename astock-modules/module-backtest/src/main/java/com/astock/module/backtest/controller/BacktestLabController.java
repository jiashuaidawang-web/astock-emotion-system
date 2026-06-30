package com.astock.module.backtest.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.backtest.api.vo.BacktestLabPageVO;
import com.astock.module.backtest.application.query.BacktestLabPageQuery;
import com.astock.module.backtest.application.service.BacktestLabQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backtests")
public class BacktestLabController {

    private final BacktestLabQueryService queryService;

    public BacktestLabController(BacktestLabQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/lab")
    public ApiResult<BacktestLabPageVO> lab(BacktestLabPageQuery query) {
        return ApiResult.success(queryService.queryLab(query));
    }
}
