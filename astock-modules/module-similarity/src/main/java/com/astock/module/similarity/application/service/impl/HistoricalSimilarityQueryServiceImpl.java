package com.astock.module.similarity.application.service.impl;

import com.astock.module.similarity.api.vo.HistoricalSimilarityPageVO;
import com.astock.module.similarity.application.aggregator.HistoricalSimilarityPageAggregator;
import com.astock.module.similarity.application.query.HistoricalSimilarityPageQuery;
import com.astock.module.similarity.application.service.HistoricalSimilarityQueryService;
import org.springframework.stereotype.Service;

@Service
public class HistoricalSimilarityQueryServiceImpl implements HistoricalSimilarityQueryService {

    private final HistoricalSimilarityPageAggregator aggregator;

    public HistoricalSimilarityQueryServiceImpl(HistoricalSimilarityPageAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public HistoricalSimilarityPageVO queryHistoricalSimilarity(HistoricalSimilarityPageQuery query) {
        return aggregator.aggregate(query);
    }
}
