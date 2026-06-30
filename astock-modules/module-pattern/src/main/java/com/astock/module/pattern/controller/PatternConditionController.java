package com.astock.module.pattern.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.pattern.api.vo.PatternConditionPageVO;
import com.astock.module.pattern.application.query.PatternConditionPageQuery;
import com.astock.module.pattern.application.service.PatternConditionQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patterns")
public class PatternConditionController {

    private final PatternConditionQueryService queryService;

    public PatternConditionController(PatternConditionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/conditions")
    public ApiResult<PatternConditionPageVO> conditions(PatternConditionPageQuery query) {
        return ApiResult.success(queryService.queryConditionPage(query));
    }
}
