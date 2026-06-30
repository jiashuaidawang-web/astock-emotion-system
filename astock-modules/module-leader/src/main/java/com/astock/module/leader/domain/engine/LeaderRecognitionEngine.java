package com.astock.module.leader.domain.engine;

public interface LeaderRecognitionEngine {
    LeaderEngineResult execute(LeaderEngineContext context);
}
