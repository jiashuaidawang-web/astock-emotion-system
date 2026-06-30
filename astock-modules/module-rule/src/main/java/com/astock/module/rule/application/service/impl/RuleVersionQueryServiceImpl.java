package com.astock.module.rule.application.service.impl;

import com.astock.module.rule.api.vo.RuleVersionManagePageVO;
import com.astock.module.rule.application.aggregator.RuleVersionManageAggregator;
import com.astock.module.rule.application.query.RuleVersionManagePageQuery;
import com.astock.module.rule.application.service.RuleVersionQueryService;
import org.springframework.stereotype.Service;

@Service
public class RuleVersionQueryServiceImpl implements RuleVersionQueryService {

    private final RuleVersionManageAggregator aggregator;

    public RuleVersionQueryServiceImpl(RuleVersionManageAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public RuleVersionManagePageVO queryManagePage(RuleVersionManagePageQuery query) {
        return aggregator.aggregate(query);
    }
}
