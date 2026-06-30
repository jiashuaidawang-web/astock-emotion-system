package com.astock.module.emotion.domain.engine;

public interface EmotionStageRecognitionEngine {
    EmotionStageEngineResult execute(EmotionStageEngineContext context);
}
