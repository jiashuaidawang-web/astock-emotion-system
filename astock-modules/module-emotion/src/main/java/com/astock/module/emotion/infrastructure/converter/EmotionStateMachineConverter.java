package com.astock.module.emotion.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.emotion.api.vo.EmotionCycleStateMachineVO;
import com.astock.module.emotion.application.query.EmotionCycleStateMachineQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EmotionStateMachineConverter implements PageBundleConverter<EmotionCycleStateMachineQuery, EmotionCycleStateMachineVO> {

    @Override
    public EmotionCycleStateMachineVO convert(EmotionCycleStateMachineQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        EmotionCycleStateMachineVO vo = new EmotionCycleStateMachineVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("emotion_stage_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setCurrentStage(toCurrentEmotionStageVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setStageScores(bundle.rows("emotion_stage_snapshot").stream().map(r -> toEmotionStageScoreVO(r, bundle)).toList());
        vo.setRecentStagePath(bundle.rows("emotion_stage_snapshot").stream().map(r -> toEmotionStagePathPointVO(r, bundle)).toList());
        vo.setPossibleTransitions(bundle.rows("emotion_stage_snapshot").stream().map(r -> toEmotionStageTransitionVO(r, bundle)).toList());
        vo.setEvidence(toEmotionStageEvidenceVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setSimilarSamples(bundle.rows("emotion_stage_snapshot").stream().map(r -> toHistoricalStageSampleVO(r, bundle)).toList());
        vo.setFollowingStat(toStageFollowingStatVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setContradictions(bundle.rows("emotion_stage_snapshot").stream().map(r -> toStageContradictionVO(r, bundle)).toList());
        vo.setManualAdjustment(toManualStageAdjustmentVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("emotion_stage_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("emotion_stage_snapshot"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "CurrentEmotionStageVO" -> "emotion_stage_snapshot";
            case "EmotionStageScoreVO" -> "emotion_stage_snapshot";
            case "EmotionStagePathPointVO" -> "emotion_stage_snapshot";
            case "EmotionStageTransitionVO" -> "emotion_stage_snapshot";
            case "EmotionStageEvidenceVO" -> "emotion_stage_snapshot";
            case "HistoricalStageSampleVO" -> "emotion_stage_snapshot";
            case "StageFollowingStatVO" -> "emotion_stage_snapshot";
            case "StageContradictionVO" -> "emotion_stage_snapshot";
            case "ManualStageAdjustmentVO" -> "emotion_stage_snapshot";
            case "EmotionCycleStateMachineVO" -> "emotion_stage_snapshot";
            default -> "emotion_stage_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private EmotionCycleStateMachineVO.CurrentEmotionStageVO toCurrentEmotionStageVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.CurrentEmotionStageVO item = new EmotionCycleStateMachineVO.CurrentEmotionStageVO();
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setStageName(MapFieldReader.string(row, "primary_stage_name"));
        item.setStageScore(MapFieldReader.decimal(row, "stage_score"));
        item.setConfidence(MapFieldReader.decimal(row, "stage_confidence"));
        item.setStageText(MapFieldReader.string(row, "stage_text"));
        return item;
    }

    private EmotionCycleStateMachineVO.EmotionStageScoreVO toEmotionStageScoreVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.EmotionStageScoreVO item = new EmotionCycleStateMachineVO.EmotionStageScoreVO();
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setStageName(MapFieldReader.string(row, "primary_stage_name"));
        item.setStageScore(MapFieldReader.decimal(row, "stage_score"));
        item.setRankNo(MapFieldReader.integer(row, "rank_no"));
        item.setFactorPercentileMatchScore(MapFieldReader.decimal(row, "factor_percentile_match_score"));
        item.setHistoricalSampleSimilarityScore(MapFieldReader.decimal(row, "historical_sample_similarity_score"));
        item.setStagePathMatchScore(MapFieldReader.decimal(row, "stage_path_match_score"));
        item.setFollowingValidationScore(MapFieldReader.decimal(row, "following_validation_score"));
        item.setManualSampleCorrectionScore(MapFieldReader.decimal(row, "manual_sample_correction_score"));
        item.setPrimary(MapFieldReader.bool(row, "primary"));
        item.setCandidate(MapFieldReader.bool(row, "candidate"));
        item.setScoreExplanation(MapFieldReader.string(row, "score_explanation"));
        return item;
    }

    private EmotionCycleStateMachineVO.EmotionStagePathPointVO toEmotionStagePathPointVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.EmotionStagePathPointVO item = new EmotionCycleStateMachineVO.EmotionStagePathPointVO();
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setStageName(MapFieldReader.string(row, "primary_stage_name"));
        item.setConfidence(MapFieldReader.decimal(row, "stage_confidence"));
        return item;
    }

    private EmotionCycleStateMachineVO.EmotionStageTransitionVO toEmotionStageTransitionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.EmotionStageTransitionVO item = new EmotionCycleStateMachineVO.EmotionStageTransitionVO();
        item.setFromStage(MapFieldReader.string(row, "from_stage"));
        item.setToStage(MapFieldReader.string(row, "to_stage"));
        item.setTransitionProbability(MapFieldReader.decimal(row, "transition_probability"));
        item.setTransitionScore(MapFieldReader.decimal(row, "transition_score"));
        item.setTransitionText(MapFieldReader.string(row, "transition_text"));
        return item;
    }

    private EmotionCycleStateMachineVO.EmotionStageEvidenceVO toEmotionStageEvidenceVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.EmotionStageEvidenceVO item = new EmotionCycleStateMachineVO.EmotionStageEvidenceVO();
        item.setFactorEvidences(stringList(row, "factor_evidences"));
        item.setSampleEvidences(stringList(row, "sample_evidences"));
        item.setPathEvidences(stringList(row, "path_evidences"));
        item.setEvidenceSummary(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

    private EmotionCycleStateMachineVO.HistoricalStageSampleVO toHistoricalStageSampleVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.HistoricalStageSampleVO item = new EmotionCycleStateMachineVO.HistoricalStageSampleVO();
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setManuallyConfirmed(MapFieldReader.bool(row, "manually_confirmed"));
        item.setSampleText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

    private EmotionCycleStateMachineVO.StageFollowingStatVO toStageFollowingStatVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.StageFollowingStatVO item = new EmotionCycleStateMachineVO.StageFollowingStatVO();
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setFuture3dAvgReturn(MapFieldReader.decimal(row, "future3d_avg_return"));
        item.setFuture5dAvgReturn(MapFieldReader.decimal(row, "future5d_avg_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setStatText(MapFieldReader.string(row, "stat_text"));
        return item;
    }

    private EmotionCycleStateMachineVO.StageContradictionVO toStageContradictionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.StageContradictionVO item = new EmotionCycleStateMachineVO.StageContradictionVO();
        item.setContradictionCode(MapFieldReader.string(row, "contradiction_code"));
        item.setContradictionName(MapFieldReader.string(row, "contradiction_name"));
        item.setDescription(MapFieldReader.string(row, "description"));
        item.setImpact(MapFieldReader.string(row, "impact"));
        return item;
    }

    private EmotionCycleStateMachineVO.ManualStageAdjustmentVO toManualStageAdjustmentVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                EmotionCycleStateMachineVO.ManualStageAdjustmentVO item = new EmotionCycleStateMachineVO.ManualStageAdjustmentVO();
        item.setAdjusted(MapFieldReader.bool(row, "adjusted"));
        item.setSystemStage(MapFieldReader.string(row, "system_stage"));
        item.setManualStage(MapFieldReader.string(row, "manual_stage"));
        item.setAdjustReason(MapFieldReader.string(row, "adjust_reason"));
        item.setAdjustedBy(MapFieldReader.string(row, "adjusted_by"));
        return item;
    }

}
