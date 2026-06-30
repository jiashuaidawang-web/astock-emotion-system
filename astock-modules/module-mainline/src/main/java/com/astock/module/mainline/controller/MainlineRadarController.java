package com.astock.module.mainline.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.mainline.api.vo.MainlineRadarPageVO;
import com.astock.module.mainline.application.query.MainlineRadarPageQuery;
import com.astock.module.mainline.application.service.MainlineRadarQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mainlines")
public class MainlineRadarController {

    private final MainlineRadarQueryService queryService;

    public MainlineRadarController(MainlineRadarQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/radar")
    public ApiResult<MainlineRadarPageVO> radar(MainlineRadarPageQuery query) {
        return ApiResult.success(queryService.queryRadar(query));
    }
}
