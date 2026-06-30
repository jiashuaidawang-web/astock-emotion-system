package com.astock.module.emotion.application.service;

import com.astock.module.emotion.api.vo.EmotionCycleStateMachineVO;
import com.astock.module.emotion.application.query.EmotionCycleStateMachineQuery;

public interface EmotionStateMachineQueryService {
    EmotionCycleStateMachineVO queryStateMachine(EmotionCycleStateMachineQuery query);
}
