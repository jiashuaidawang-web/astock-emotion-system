package com.astock.module.emotion.application.service.impl;

import com.astock.module.emotion.api.vo.EmotionCycleStateMachineVO;
import com.astock.module.emotion.application.aggregator.EmotionStateMachineAggregator;
import com.astock.module.emotion.application.query.EmotionCycleStateMachineQuery;
import com.astock.module.emotion.application.service.EmotionStateMachineQueryService;
import org.springframework.stereotype.Service;

@Service
public class EmotionStateMachineQueryServiceImpl implements EmotionStateMachineQueryService {

    private final EmotionStateMachineAggregator aggregator;

    public EmotionStateMachineQueryServiceImpl(EmotionStateMachineAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public EmotionCycleStateMachineVO queryStateMachine(EmotionCycleStateMachineQuery query) {
        return aggregator.aggregate(query);
    }
}
