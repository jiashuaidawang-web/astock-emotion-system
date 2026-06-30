package com.astock.app.controller;

import com.astock.common.api.ApiResult;
import com.astock.common.lineage.PageFieldLineageVO;
import com.astock.infrastructure.lineage.PageFieldLineageQueryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/page-lineage")
public class PageFieldLineageController {
    private final PageFieldLineageQueryService queryService;

    public PageFieldLineageController(PageFieldLineageQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{pageCode}")
    public ApiResult<List<PageFieldLineageVO>> pageLineage(@PathVariable String pageCode) {
        return ApiResult.success(queryService.queryByPageCode(pageCode));
    }

    @GetMapping("/{pageCode}/{voClassName}")
    public ApiResult<List<PageFieldLineageVO>> voLineage(@PathVariable String pageCode,
                                                         @PathVariable String voClassName) {
        return ApiResult.success(queryService.queryByVoClass(pageCode, voClassName));
    }
}
