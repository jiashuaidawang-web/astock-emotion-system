package com.astock.module.spider.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.spider.job.SpiderJob;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spider")
@RequiredArgsConstructor
public class SpiderController {

    private final SpiderJob spiderJob;

    @GetMapping("/dc/daily")
    public ApiResult<Map<String, Object>> runEastMoneyDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradeDate) {
        LocalDate realTradeDate = tradeDate == null ? LocalDate.now() : tradeDate;
        return ApiResult.success(spiderJob.runEastMoneyDaily(realTradeDate));
    }

    @GetMapping("/ths/daily")
    public ApiResult<Map<String, Object>> runThsDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradeDate) {
        LocalDate realTradeDate = tradeDate == null ? LocalDate.now() : tradeDate;
        return ApiResult.success(spiderJob.runThsDaily(realTradeDate));
    }

    @GetMapping("/validate")
    public ApiResult<Map<String, Object>> validate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tradeDate) {
        LocalDate realTradeDate = tradeDate == null ? LocalDate.now() : tradeDate;
        return ApiResult.success(spiderJob.validate(realTradeDate));
    }
}
