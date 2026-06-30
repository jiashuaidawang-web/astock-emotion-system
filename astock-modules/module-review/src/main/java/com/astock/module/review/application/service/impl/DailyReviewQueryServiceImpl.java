package com.astock.module.review.application.service.impl;

import com.astock.module.review.api.vo.DailyReviewWorkbenchVO;
import com.astock.module.review.application.aggregator.DailyReviewWorkbenchAggregator;
import com.astock.module.review.application.query.DailyReviewWorkbenchQuery;
import com.astock.module.review.application.service.DailyReviewQueryService;
import org.springframework.stereotype.Service;

@Service
public class DailyReviewQueryServiceImpl implements DailyReviewQueryService {

    private final DailyReviewWorkbenchAggregator aggregator;

    public DailyReviewQueryServiceImpl(DailyReviewWorkbenchAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public DailyReviewWorkbenchVO queryWorkbench(DailyReviewWorkbenchQuery query) {
        return aggregator.aggregate(query);
    }
}
