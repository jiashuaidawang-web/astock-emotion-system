package com.astock.module.risk.application.service.impl;

import com.astock.module.risk.api.vo.RiskControlPageVO;
import com.astock.module.risk.application.aggregator.RiskControlAggregator;
import com.astock.module.risk.application.query.RiskControlPageQuery;
import com.astock.module.risk.application.service.RiskControlQueryService;
import org.springframework.stereotype.Service;

@Service
public class RiskControlQueryServiceImpl implements RiskControlQueryService {

    private final RiskControlAggregator aggregator;

    public RiskControlQueryServiceImpl(RiskControlAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public RiskControlPageVO queryRiskControlPage(RiskControlPageQuery query) {
        return aggregator.aggregate(query);
    }
}
