package com.astock.module.rule.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.rule.api.vo.RuleVersionManagePageVO;
import com.astock.module.rule.application.query.RuleVersionManagePageQuery;
import com.astock.module.rule.application.service.RuleVersionQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
public class RuleVersionManageController {

    private final RuleVersionQueryService queryService;

    public RuleVersionManageController(RuleVersionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/versions/page")
    public ApiResult<RuleVersionManagePageVO> page(RuleVersionManagePageQuery query) {
        return ApiResult.success(queryService.queryManagePage(query));
    }
}
