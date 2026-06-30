package com.astock.module.leader.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.leader.api.vo.LeaderLadderPageVO;
import com.astock.module.leader.application.query.LeaderLadderPageQuery;
import com.astock.module.leader.application.service.LeaderLadderQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaders")
public class LeaderLadderController {

    private final LeaderLadderQueryService queryService;

    public LeaderLadderController(LeaderLadderQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/ladder")
    public ApiResult<LeaderLadderPageVO> ladder(LeaderLadderPageQuery query) {
        return ApiResult.success(queryService.queryLadderPage(query));
    }
}
