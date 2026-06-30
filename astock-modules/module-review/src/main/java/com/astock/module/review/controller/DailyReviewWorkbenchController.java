package com.astock.module.review.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.review.api.vo.DailyReviewWorkbenchVO;
import com.astock.module.review.application.query.DailyReviewWorkbenchQuery;
import com.astock.module.review.application.service.DailyReviewQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews/daily")
public class DailyReviewWorkbenchController {

    private final DailyReviewQueryService queryService;

    public DailyReviewWorkbenchController(DailyReviewQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/workbench")
    public ApiResult<DailyReviewWorkbenchVO> workbench(DailyReviewWorkbenchQuery query) {
        return ApiResult.success(queryService.queryWorkbench(query));
    }
}
