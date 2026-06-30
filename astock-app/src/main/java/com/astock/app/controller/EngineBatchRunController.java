package com.astock.app.controller;

import com.astock.app.orchestration.EngineBatchOrchestrationService;
import com.astock.app.orchestration.EngineBatchRunRequest;
import com.astock.app.orchestration.EngineBatchRunResult;
import com.astock.common.api.ApiResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/engines/batch")
public class EngineBatchRunController {
    private final EngineBatchOrchestrationService orchestrationService;

    public EngineBatchRunController(EngineBatchOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/daily/run")
    public ApiResult<EngineBatchRunResult> runDailyBatch(@RequestBody EngineBatchRunRequest request) {
        return ApiResult.success(orchestrationService.runDailyBatch(request));
    }
}
