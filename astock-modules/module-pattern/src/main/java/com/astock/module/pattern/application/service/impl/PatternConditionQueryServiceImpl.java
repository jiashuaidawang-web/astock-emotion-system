package com.astock.module.pattern.application.service.impl;

import com.astock.module.pattern.api.vo.PatternConditionPageVO;
import com.astock.module.pattern.application.aggregator.PatternConditionAggregator;
import com.astock.module.pattern.application.query.PatternConditionPageQuery;
import com.astock.module.pattern.application.service.PatternConditionQueryService;
import org.springframework.stereotype.Service;

@Service
public class PatternConditionQueryServiceImpl implements PatternConditionQueryService {

    private final PatternConditionAggregator aggregator;

    public PatternConditionQueryServiceImpl(PatternConditionAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public PatternConditionPageVO queryConditionPage(PatternConditionPageQuery query) {
        return aggregator.aggregate(query);
    }
}
