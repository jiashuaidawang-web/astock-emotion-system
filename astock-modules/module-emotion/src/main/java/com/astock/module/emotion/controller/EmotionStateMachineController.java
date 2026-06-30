package com.astock.module.emotion.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.emotion.api.vo.EmotionCycleStateMachineVO;
import com.astock.module.emotion.application.query.EmotionCycleStateMachineQuery;
import com.astock.module.emotion.application.service.EmotionStateMachineQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emotion-cycle")
public class EmotionStateMachineController {

    private final EmotionStateMachineQueryService queryService;

    public EmotionStateMachineController(EmotionStateMachineQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/state-machine")
    public ApiResult<EmotionCycleStateMachineVO> stateMachine(EmotionCycleStateMachineQuery query) {
        return ApiResult.success(queryService.queryStateMachine(query));
    }
}
