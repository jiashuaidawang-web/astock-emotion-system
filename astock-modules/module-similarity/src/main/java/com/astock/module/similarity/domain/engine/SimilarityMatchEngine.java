package com.astock.module.similarity.domain.engine;

public interface SimilarityMatchEngine {
    SimilarityMatchEngineResult execute(SimilarityMatchEngineContext context);
}
