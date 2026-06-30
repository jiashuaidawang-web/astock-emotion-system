package com.astock.module.similarity.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.similarity.api.vo.HistoricalSimilarityPageVO;
import com.astock.module.similarity.application.query.HistoricalSimilarityPageQuery;
import com.astock.module.similarity.application.service.HistoricalSimilarityQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/similarity")
public class HistoricalSimilarityController {

    private final HistoricalSimilarityQueryService queryService;

    public HistoricalSimilarityController(HistoricalSimilarityQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/market")
    public ApiResult<HistoricalSimilarityPageVO> market(HistoricalSimilarityPageQuery query) {
        return ApiResult.success(queryService.queryHistoricalSimilarity(query));
    }
}
