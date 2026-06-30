package com.astock.module.sample.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.sample.api.vo.HistoricalCycleSamplePageVO;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;
import com.astock.module.sample.application.service.HistoricalCycleSampleQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cycle-samples")
public class HistoricalCycleSampleController {

    private final HistoricalCycleSampleQueryService queryService;

    public HistoricalCycleSampleController(HistoricalCycleSampleQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/page")
    public ApiResult<HistoricalCycleSamplePageVO> page(HistoricalCycleSamplePageQuery query) {
        return ApiResult.success(queryService.querySamplePage(query));
    }
}
