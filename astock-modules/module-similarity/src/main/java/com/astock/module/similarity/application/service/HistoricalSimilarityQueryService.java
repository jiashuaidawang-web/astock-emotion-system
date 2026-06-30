package com.astock.module.similarity.application.service;

import com.astock.module.similarity.api.vo.HistoricalSimilarityPageVO;
import com.astock.module.similarity.application.query.HistoricalSimilarityPageQuery;

public interface HistoricalSimilarityQueryService {
    HistoricalSimilarityPageVO queryHistoricalSimilarity(HistoricalSimilarityPageQuery query);
}
