package com.astock.module.mainline.application.service.impl;

import com.astock.module.mainline.api.vo.MainlineRadarPageVO;
import com.astock.module.mainline.application.aggregator.MainlineRadarAggregator;
import com.astock.module.mainline.application.query.MainlineRadarPageQuery;
import com.astock.module.mainline.application.service.MainlineRadarQueryService;
import org.springframework.stereotype.Service;

@Service
public class MainlineRadarQueryServiceImpl implements MainlineRadarQueryService {

    private final MainlineRadarAggregator aggregator;

    public MainlineRadarQueryServiceImpl(MainlineRadarAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public MainlineRadarPageVO queryRadar(MainlineRadarPageQuery query) {
        return aggregator.aggregate(query);
    }
}
