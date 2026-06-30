package com.astock.module.leader.application.service.impl;

import com.astock.module.leader.api.vo.LeaderLadderPageVO;
import com.astock.module.leader.application.aggregator.LeaderLadderAggregator;
import com.astock.module.leader.application.query.LeaderLadderPageQuery;
import com.astock.module.leader.application.service.LeaderLadderQueryService;
import org.springframework.stereotype.Service;

@Service
public class LeaderLadderQueryServiceImpl implements LeaderLadderQueryService {

    private final LeaderLadderAggregator aggregator;

    public LeaderLadderQueryServiceImpl(LeaderLadderAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public LeaderLadderPageVO queryLadderPage(LeaderLadderPageQuery query) {
        return aggregator.aggregate(query);
    }
}
