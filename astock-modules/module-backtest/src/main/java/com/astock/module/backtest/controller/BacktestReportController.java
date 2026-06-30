package com.astock.module.backtest.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.backtest.api.vo.BacktestReportDetailVO;
import com.astock.module.backtest.application.query.BacktestReportDetailQuery;
import com.astock.module.backtest.application.service.BacktestReportQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtests/reports")
public class BacktestReportController {

    private final BacktestReportQueryService queryService;

    public BacktestReportController(BacktestReportQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{reportId}")
    public ApiResult<BacktestReportDetailVO> detail(@PathVariable Long reportId, BacktestReportDetailQuery query) {
        query.setReportId(reportId);
        return ApiResult.success(queryService.queryReportDetail(query));
    }
}
