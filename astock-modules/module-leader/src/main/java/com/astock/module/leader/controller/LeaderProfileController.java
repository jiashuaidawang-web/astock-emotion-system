package com.astock.module.leader.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.leader.api.vo.LeaderProfilePageVO;
import com.astock.module.leader.application.query.LeaderProfilePageQuery;
import com.astock.module.leader.application.service.LeaderProfileQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaders")
public class LeaderProfileController {

    private final LeaderProfileQueryService queryService;

    public LeaderProfileController(LeaderProfileQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{stockCode}/profile")
    public ApiResult<LeaderProfilePageVO> profile(@PathVariable String stockCode, LeaderProfilePageQuery query) {
        query.setStockCode(stockCode);
        return ApiResult.success(queryService.queryProfile(query));
    }
}
