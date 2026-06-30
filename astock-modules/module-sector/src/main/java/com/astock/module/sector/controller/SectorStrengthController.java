package com.astock.module.sector.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.sector.api.vo.SectorStrengthPageVO;
import com.astock.module.sector.application.query.SectorStrengthPageQuery;
import com.astock.module.sector.application.service.SectorStrengthQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sectors")
public class SectorStrengthController {

    private final SectorStrengthQueryService queryService;

    public SectorStrengthController(SectorStrengthQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/strength")
    public ApiResult<SectorStrengthPageVO> strength(SectorStrengthPageQuery query) {
        return ApiResult.success(queryService.queryStrengthPage(query));
    }
}
