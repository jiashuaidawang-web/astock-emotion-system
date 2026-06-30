package com.astock.module.sector.application.service.impl;

import com.astock.module.sector.api.vo.SectorStrengthPageVO;
import com.astock.module.sector.application.aggregator.SectorStrengthAggregator;
import com.astock.module.sector.application.query.SectorStrengthPageQuery;
import com.astock.module.sector.application.service.SectorStrengthQueryService;
import org.springframework.stereotype.Service;

@Service
public class SectorStrengthQueryServiceImpl implements SectorStrengthQueryService {

    private final SectorStrengthAggregator aggregator;

    public SectorStrengthQueryServiceImpl(SectorStrengthAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public SectorStrengthPageVO queryStrengthPage(SectorStrengthPageQuery query) {
        return aggregator.aggregate(query);
    }
}
