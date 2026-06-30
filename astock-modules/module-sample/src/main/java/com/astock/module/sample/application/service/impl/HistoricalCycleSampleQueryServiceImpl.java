package com.astock.module.sample.application.service.impl;

import com.astock.module.sample.api.vo.HistoricalCycleSamplePageVO;
import com.astock.module.sample.application.aggregator.HistoricalCycleSamplePageAggregator;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;
import com.astock.module.sample.application.service.HistoricalCycleSampleQueryService;
import org.springframework.stereotype.Service;

@Service
public class HistoricalCycleSampleQueryServiceImpl implements HistoricalCycleSampleQueryService {

    private final HistoricalCycleSamplePageAggregator aggregator;

    public HistoricalCycleSampleQueryServiceImpl(HistoricalCycleSamplePageAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public HistoricalCycleSamplePageVO querySamplePage(HistoricalCycleSamplePageQuery query) {
        return aggregator.aggregate(query);
    }
}
