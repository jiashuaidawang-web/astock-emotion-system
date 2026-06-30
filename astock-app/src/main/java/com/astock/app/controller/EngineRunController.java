package com.astock.app.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.agentaudit.domain.engine.AgentAuditEngineContext;
import com.astock.module.agentaudit.domain.engine.AgentAuditEngineResult;
import com.astock.module.agentaudit.domain.engine.AgentAuditExecutor;
import com.astock.module.backtest.domain.engine.BacktestEngineContext;
import com.astock.module.backtest.domain.engine.BacktestEngineResult;
import com.astock.module.backtest.domain.engine.BacktestExecutor;
import com.astock.module.emotion.domain.engine.EmotionStageEngineContext;
import com.astock.module.emotion.domain.engine.EmotionStageEngineResult;
import com.astock.module.emotion.domain.engine.EmotionStageRecognitionEngine;
import com.astock.module.leader.domain.engine.LeaderEngineContext;
import com.astock.module.leader.domain.engine.LeaderEngineResult;
import com.astock.module.leader.domain.engine.LeaderRecognitionEngine;
import com.astock.module.mainline.domain.engine.MainlineEngineContext;
import com.astock.module.mainline.domain.engine.MainlineEngineResult;
import com.astock.module.mainline.domain.engine.MainlineRecognitionEngine;
import com.astock.module.pattern.domain.engine.PatternConditionEngine;
import com.astock.module.pattern.domain.engine.PatternEngineContext;
import com.astock.module.pattern.domain.engine.PatternEngineResult;
import com.astock.module.risk.domain.engine.RiskControlEngine;
import com.astock.module.risk.domain.engine.RiskEngineContext;
import com.astock.module.risk.domain.engine.RiskEngineResult;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngine;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngineContext;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngineResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/engines")
public class EngineRunController {
    private final EmotionStageRecognitionEngine emotionEngine;
    private final SimilarityMatchEngine similarityEngine;
    private final MainlineRecognitionEngine mainlineEngine;
    private final LeaderRecognitionEngine leaderEngine;
    private final PatternConditionEngine patternEngine;
    private final RiskControlEngine riskEngine;
    private final BacktestExecutor backtestExecutor;
    private final AgentAuditExecutor agentAuditExecutor;

    public EngineRunController(EmotionStageRecognitionEngine emotionEngine,
                               SimilarityMatchEngine similarityEngine,
                               MainlineRecognitionEngine mainlineEngine,
                               LeaderRecognitionEngine leaderEngine,
                               PatternConditionEngine patternEngine,
                               RiskControlEngine riskEngine,
                               BacktestExecutor backtestExecutor,
                               AgentAuditExecutor agentAuditExecutor) {
        this.emotionEngine = emotionEngine;
        this.similarityEngine = similarityEngine;
        this.mainlineEngine = mainlineEngine;
        this.leaderEngine = leaderEngine;
        this.patternEngine = patternEngine;
        this.riskEngine = riskEngine;
        this.backtestExecutor = backtestExecutor;
        this.agentAuditExecutor = agentAuditExecutor;
    }

    @PostMapping("/emotion-stage/run")
    public ApiResult<EmotionStageEngineResult> runEmotion(@RequestBody EngineRunRequest request) {
        EmotionStageEngineContext context = new EmotionStageEngineContext();
        fill(context, request);
        return ApiResult.success(emotionEngine.execute(context));
    }

    @PostMapping("/similarity/run")
    public ApiResult<SimilarityMatchEngineResult> runSimilarity(@RequestBody EngineRunRequest request) {
        SimilarityMatchEngineContext context = new SimilarityMatchEngineContext();
        fill(context, request);
        return ApiResult.success(similarityEngine.execute(context));
    }

    @PostMapping("/mainline/run")
    public ApiResult<MainlineEngineResult> runMainline(@RequestBody EngineRunRequest request) {
        MainlineEngineContext context = new MainlineEngineContext();
        fill(context, request);
        return ApiResult.success(mainlineEngine.execute(context));
    }

    @PostMapping("/leader/run")
    public ApiResult<LeaderEngineResult> runLeader(@RequestBody EngineRunRequest request) {
        LeaderEngineContext context = new LeaderEngineContext();
        fill(context, request);
        return ApiResult.success(leaderEngine.execute(context));
    }

    @PostMapping("/pattern/run")
    public ApiResult<PatternEngineResult> runPattern(@RequestBody EngineRunRequest request) {
        PatternEngineContext context = new PatternEngineContext();
        fill(context, request);
        return ApiResult.success(patternEngine.execute(context));
    }

    @PostMapping("/risk/run")
    public ApiResult<RiskEngineResult> runRisk(@RequestBody EngineRunRequest request) {
        RiskEngineContext context = new RiskEngineContext();
        fill(context, request);
        return ApiResult.success(riskEngine.execute(context));
    }

    @PostMapping("/backtest/run")
    public ApiResult<BacktestEngineResult> runBacktest(@RequestBody EngineRunRequest request) {
        BacktestEngineContext context = new BacktestEngineContext();
        fill(context, request);
        return ApiResult.success(backtestExecutor.execute(context));
    }

    @PostMapping("/agent-audit/run")
    public ApiResult<AgentAuditEngineResult> runAgentAudit(@RequestBody EngineRunRequest request) {
        AgentAuditEngineContext context = new AgentAuditEngineContext();
        fill(context, request);
        return ApiResult.success(agentAuditExecutor.execute(context));
    }

    private void fill(EmotionStageEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(SimilarityMatchEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(MainlineEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(LeaderEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(PatternEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(RiskEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(BacktestEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }
    private void fill(AgentAuditEngineContext c, EngineRunRequest r) { c.setTradeDate(r.tradeDate); c.setMarketScope(r.marketScope); c.setRuleVersionId(r.ruleVersionId); c.setDataCheckEnabled(r.dataCheckEnabled); c.setParamJson(r.paramJson); }

    public static class EngineRunRequest {
        public LocalDate tradeDate;
        public String marketScope;
        public Long ruleVersionId;
        public Boolean dataCheckEnabled = true;
        public String paramJson;
    }
}
