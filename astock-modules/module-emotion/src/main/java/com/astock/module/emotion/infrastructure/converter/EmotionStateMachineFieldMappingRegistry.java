package com.astock.module.emotion.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmotionStateMachineFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_03_EMOTION_STATE_MACHINE";
    }

    @Override
    public String voClassName() {
        return "EmotionCycleStateMachineVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("EmotionCycleStateMachineVO.tradeDate", "CLICKHOUSE", "emotion_stage_snapshot", "trade_date", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("EmotionCycleStateMachineVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("EmotionCycleStateMachineVO.currentStage", "CLICKHOUSE", "emotion_stage_snapshot", "current_stage", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.stageScores", "CLICKHOUSE", "emotion_stage_snapshot", "stage_scores", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.recentStagePath", "CLICKHOUSE", "emotion_stage_snapshot", "recent_stage_path", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.possibleTransitions", "CLICKHOUSE", "emotion_stage_snapshot", "possible_transitions", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.evidence", "CLICKHOUSE", "emotion_stage_snapshot", "evidence", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.similarSamples", "CLICKHOUSE", "emotion_stage_snapshot", "similar_samples", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.followingStat", "CLICKHOUSE", "emotion_stage_snapshot", "following_stat", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.contradictions", "CLICKHOUSE", "emotion_stage_snapshot", "contradictions", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.manualAdjustment", "CLICKHOUSE", "emotion_stage_snapshot", "manual_adjustment", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.conclusion", "CLICKHOUSE", "emotion_stage_snapshot", "evidence_json", "", false),
                new FieldMapping("EmotionCycleStateMachineVO.riskTips", "CLICKHOUSE", "emotion_stage_snapshot", "risk_json", "", false),
                new FieldMapping("CurrentEmotionStageVO.stageCode", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage", "", false),
                new FieldMapping("CurrentEmotionStageVO.stageName", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage_name", "", false),
                new FieldMapping("CurrentEmotionStageVO.stageScore", "CLICKHOUSE", "emotion_stage_snapshot", "stage_score", "", false),
                new FieldMapping("CurrentEmotionStageVO.confidence", "CLICKHOUSE", "emotion_stage_snapshot", "stage_confidence", "", false),
                new FieldMapping("CurrentEmotionStageVO.stageText", "CLICKHOUSE", "emotion_stage_snapshot", "stage_text", "", false),
                new FieldMapping("EmotionStageScoreVO.stageCode", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage", "", false),
                new FieldMapping("EmotionStageScoreVO.stageName", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage_name", "", false),
                new FieldMapping("EmotionStageScoreVO.stageScore", "CLICKHOUSE", "emotion_stage_snapshot", "stage_score", "", false),
                new FieldMapping("EmotionStageScoreVO.rankNo", "CLICKHOUSE", "emotion_stage_snapshot", "rank_no", "", false),
                new FieldMapping("EmotionStageScoreVO.factorPercentileMatchScore", "CLICKHOUSE", "emotion_stage_snapshot", "factor_percentile_match_score", "", false),
                new FieldMapping("EmotionStageScoreVO.historicalSampleSimilarityScore", "CLICKHOUSE", "emotion_stage_snapshot", "historical_sample_similarity_score", "", false),
                new FieldMapping("EmotionStageScoreVO.stagePathMatchScore", "CLICKHOUSE", "emotion_stage_snapshot", "stage_path_match_score", "", false),
                new FieldMapping("EmotionStageScoreVO.followingValidationScore", "CLICKHOUSE", "emotion_stage_snapshot", "following_validation_score", "", false),
                new FieldMapping("EmotionStageScoreVO.manualSampleCorrectionScore", "CLICKHOUSE", "emotion_stage_snapshot", "manual_sample_correction_score", "", false),
                new FieldMapping("EmotionStageScoreVO.primary", "CLICKHOUSE", "emotion_stage_snapshot", "primary", "", false),
                new FieldMapping("EmotionStageScoreVO.candidate", "CLICKHOUSE", "emotion_stage_snapshot", "candidate", "", false),
                new FieldMapping("EmotionStageScoreVO.scoreExplanation", "CLICKHOUSE", "emotion_stage_snapshot", "score_explanation", "", false),
                new FieldMapping("EmotionStagePathPointVO.tradeDate", "CLICKHOUSE", "emotion_stage_snapshot", "trade_date", "", false),
                new FieldMapping("EmotionStagePathPointVO.stageCode", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage", "", false),
                new FieldMapping("EmotionStagePathPointVO.stageName", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage_name", "", false),
                new FieldMapping("EmotionStagePathPointVO.confidence", "CLICKHOUSE", "emotion_stage_snapshot", "stage_confidence", "", false),
                new FieldMapping("EmotionStageTransitionVO.fromStage", "CLICKHOUSE", "emotion_stage_snapshot", "from_stage", "", false),
                new FieldMapping("EmotionStageTransitionVO.toStage", "CLICKHOUSE", "emotion_stage_snapshot", "to_stage", "", false),
                new FieldMapping("EmotionStageTransitionVO.transitionProbability", "CLICKHOUSE", "emotion_stage_snapshot", "transition_probability", "", false),
                new FieldMapping("EmotionStageTransitionVO.transitionScore", "CLICKHOUSE", "emotion_stage_snapshot", "transition_score", "", false),
                new FieldMapping("EmotionStageTransitionVO.transitionText", "CLICKHOUSE", "emotion_stage_snapshot", "transition_text", "", false),
                new FieldMapping("EmotionStageEvidenceVO.factorEvidences", "CLICKHOUSE", "emotion_stage_snapshot", "factor_evidences", "", false),
                new FieldMapping("EmotionStageEvidenceVO.sampleEvidences", "CLICKHOUSE", "emotion_stage_snapshot", "sample_evidences", "", false),
                new FieldMapping("EmotionStageEvidenceVO.pathEvidences", "CLICKHOUSE", "emotion_stage_snapshot", "path_evidences", "", false),
                new FieldMapping("EmotionStageEvidenceVO.evidenceSummary", "CLICKHOUSE", "emotion_stage_snapshot", "evidence_json", "", false),
                new FieldMapping("HistoricalStageSampleVO.sampleId", "CLICKHOUSE", "emotion_stage_snapshot", "sample_id", "", false),
                new FieldMapping("HistoricalStageSampleVO.tradeDate", "CLICKHOUSE", "emotion_stage_snapshot", "trade_date", "", false),
                new FieldMapping("HistoricalStageSampleVO.stageCode", "CLICKHOUSE", "emotion_stage_snapshot", "primary_stage", "", false),
                new FieldMapping("HistoricalStageSampleVO.similarityScore", "CLICKHOUSE", "emotion_stage_snapshot", "similarity_score", "", false),
                new FieldMapping("HistoricalStageSampleVO.manuallyConfirmed", "CLICKHOUSE", "emotion_stage_snapshot", "manually_confirmed", "", false),
                new FieldMapping("HistoricalStageSampleVO.sampleText", "CLICKHOUSE", "emotion_stage_snapshot", "evidence_json", "", false),
                new FieldMapping("StageFollowingStatVO.sampleCount", "CLICKHOUSE", "emotion_stage_snapshot", "sample_count", "", false),
                new FieldMapping("StageFollowingStatVO.future3dAvgReturn", "CLICKHOUSE", "emotion_stage_snapshot", "future3d_avg_return", "", false),
                new FieldMapping("StageFollowingStatVO.future5dAvgReturn", "CLICKHOUSE", "emotion_stage_snapshot", "future5d_avg_return", "", false),
                new FieldMapping("StageFollowingStatVO.maxDrawdown", "CLICKHOUSE", "emotion_stage_snapshot", "max_drawdown", "", false),
                new FieldMapping("StageFollowingStatVO.statText", "CLICKHOUSE", "emotion_stage_snapshot", "stat_text", "", false),
                new FieldMapping("StageContradictionVO.contradictionCode", "CLICKHOUSE", "emotion_stage_snapshot", "contradiction_code", "", false),
                new FieldMapping("StageContradictionVO.contradictionName", "CLICKHOUSE", "emotion_stage_snapshot", "contradiction_name", "", false),
                new FieldMapping("StageContradictionVO.description", "CLICKHOUSE", "emotion_stage_snapshot", "description", "", false),
                new FieldMapping("StageContradictionVO.impact", "CLICKHOUSE", "emotion_stage_snapshot", "impact", "", false),
                new FieldMapping("ManualStageAdjustmentVO.adjusted", "CLICKHOUSE", "emotion_stage_snapshot", "adjusted", "", false),
                new FieldMapping("ManualStageAdjustmentVO.systemStage", "CLICKHOUSE", "emotion_stage_snapshot", "system_stage", "", false),
                new FieldMapping("ManualStageAdjustmentVO.manualStage", "CLICKHOUSE", "emotion_stage_snapshot", "manual_stage", "", false),
                new FieldMapping("ManualStageAdjustmentVO.adjustReason", "CLICKHOUSE", "emotion_stage_snapshot", "adjust_reason", "", false),
                new FieldMapping("ManualStageAdjustmentVO.adjustedBy", "CLICKHOUSE", "emotion_stage_snapshot", "adjusted_by", "", false)
        );
    }
}
