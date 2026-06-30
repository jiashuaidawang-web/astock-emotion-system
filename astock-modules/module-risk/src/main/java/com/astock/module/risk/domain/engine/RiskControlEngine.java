package com.astock.module.risk.domain.engine;

public interface RiskControlEngine {
    RiskEngineResult execute(RiskEngineContext context);
}
