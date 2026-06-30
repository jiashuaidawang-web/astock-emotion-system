package com.astock.app.controller;

import com.astock.common.api.ApiResult;
import com.astock.common.data.DataQualityCheckVO;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/data-quality")
public class DataQualityController {
    private final DataQualityQueryService queryService;

    public DataQualityController(DataQualityQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/trade-date/{tradeDate}")
    public ApiResult<List<DataQualityCheckVO>> checkByTradeDate(@PathVariable LocalDate tradeDate) {
        return ApiResult.success(queryService.queryPersistedChecks(tradeDate));
    }

    @GetMapping("/page/{pageCode}")
    public ApiResult<PageDataQualityVO> checkPage(@PathVariable String pageCode,
                                                  @RequestParam LocalDate tradeDate,
                                                  @RequestParam(required = false, defaultValue = "A_SHARE") String marketScope) {
        return ApiResult.success(queryService.checkPage(pageCode, tradeDate, marketScope,
                List.of(new RequiredSnapshot("MARKET_FACTOR", "market_factor_snapshot", true))));
    }
}
