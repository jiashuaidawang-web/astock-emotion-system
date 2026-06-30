package com.astock.module.risk.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.risk.api.vo.RiskControlPageVO;
import com.astock.module.risk.application.query.RiskControlPageQuery;
import com.astock.module.risk.application.service.RiskControlQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risks")
public class RiskControlController {

    private final RiskControlQueryService queryService;

    public RiskControlController(RiskControlQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/control")
    public ApiResult<RiskControlPageVO> control(RiskControlPageQuery query) {
        return ApiResult.success(queryService.queryRiskControlPage(query));
    }
}
