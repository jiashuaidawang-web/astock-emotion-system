package com.astock.module.similarity.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.similarity.domain.model.SimilarityDimensionType;
import com.astock.module.similarity.domain.model.SimilarityFeatureVector;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SimilarityFeatureVectorExtractor {
    private final FutureFieldGuardService futureFieldGuardService;

    public SimilarityFeatureVectorExtractor(FutureFieldGuardService futureFieldGuardService) {
        this.futureFieldGuardService = futureFieldGuardService;
    }

    public SimilarityFeatureVector current(LocalDate tradeDate, PageSnapshotBundle bundle) {
        Map<String, Object> market = bundle.firstRow("market_factor_snapshot");
        Map<String, Object> limitEco = bundle.firstRow("limit_up_down_ecology_snapshot");
        Map<String, Object> emotion = bundle.firstRow("emotion_stage_snapshot");
        Map<String, Object> mainline = bundle.firstRow("mainline_daily_snapshot");
        Map<String, Object> leader = bundle.firstRow("leader_daily_snapshot");

        SimilarityFeatureVector vector = new SimilarityFeatureVector();
        vector.setTradeDate(tradeDate);
        vector.setStageCode(readStage(emotion));

        vector.put(SimilarityDimensionType.MARKET_BREADTH, marketBreadth(market));
        vector.put(SimilarityDimensionType.TURNOVER_VOLUME, readSafe(market, "turnover_percentile", "amount_percentile", "turnover_heat_score"));
        vector.put(SimilarityDimensionType.INDEX_POSITION, readSafe(market, "index_position_score", "index_percentile", "index_location_score"));
        vector.put(SimilarityDimensionType.LIMIT_ECOLOGY, limitEcology(limitEco));
        vector.put(SimilarityDimensionType.LEADER_LADDER, readSafe(limitEco, "max_board_height_score", "ladder_height_score", "max_board_height"));
        vector.put(SimilarityDimensionType.LOSS_EFFECT, readSafe(market, "loss_effect_score", "loss_pressure_score"));
        vector.put(SimilarityDimensionType.STAGE_PATH, readSafe(emotion, "stage_confidence", "stage_score"));
        vector.put(SimilarityDimensionType.MAINLINE_STRUCTURE, readSafe(mainline, "mainline_strength_score", "strength_score"));
        vector.put(SimilarityDimensionType.LEADER_FEEDBACK, readSafe(leader, "negative_feedback_score", "leader_negative_feedback_score"));
        return normalize(vector);
    }

    public SimilarityFeatureVector historical(Map<String, Object> sample) {
        futureFieldGuardService.assertRowNotUsedDirectlyForMatching(sample, List.of(
                "market_breadth_score",
                "turnover_percentile",
                "index_position_score",
                "limit_ecology_score",
                "leader_ladder_score",
                "loss_effect_score",
                "stage_path_score",
                "mainline_structure_score",
                "leader_feedback_score",
                "stage_code",
                "sample_type",
                "trade_date"
        ));

        SimilarityFeatureVector vector = new SimilarityFeatureVector();
        vector.setSampleId(readId(sample));
        vector.setTradeDate(MapFieldReader.localDate(sample, "trade_date"));
        vector.setStageCode(readStage(sample));

        vector.put(SimilarityDimensionType.MARKET_BREADTH, readSafe(sample, "market_breadth_score", "breadth_score"));
        vector.put(SimilarityDimensionType.TURNOVER_VOLUME, readSafe(sample, "turnover_percentile", "turnover_score", "turnover_heat_score"));
        vector.put(SimilarityDimensionType.INDEX_POSITION, readSafe(sample, "index_position_score", "index_percentile"));
        vector.put(SimilarityDimensionType.LIMIT_ECOLOGY, readSafe(sample, "limit_ecology_score", "limit_up_ecology_score"));
        vector.put(SimilarityDimensionType.LEADER_LADDER, readSafe(sample, "leader_ladder_score", "ladder_height_score"));
        vector.put(SimilarityDimensionType.LOSS_EFFECT, readSafe(sample, "loss_effect_score", "loss_pressure_score"));
        vector.put(SimilarityDimensionType.STAGE_PATH, readSafe(sample, "stage_path_score", "stage_confidence", "stage_score"));
        vector.put(SimilarityDimensionType.MAINLINE_STRUCTURE, readSafe(sample, "mainline_structure_score", "mainline_strength_score"));
        vector.put(SimilarityDimensionType.LEADER_FEEDBACK, readSafe(sample, "leader_feedback_score", "negative_feedback_score"));
        return normalize(vector);
    }

    private SimilarityFeatureVector normalize(SimilarityFeatureVector vector) {
        for (SimilarityDimensionType dimension : SimilarityDimensionType.orderedDimensions()) {
            vector.put(dimension, clamp(vector.get(dimension)));
        }
        return vector;
    }

    private BigDecimal marketBreadth(Map<String, Object> row) {
        BigDecimal rise = readSafe(row, "rise_count", "up_count", "rising_count");
        BigDecimal fall = readSafe(row, "fall_count", "down_count", "falling_count");
        BigDecimal flat = readSafe(row, "flat_count", "unchanged_count");
        BigDecimal total = rise.add(fall).add(flat);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return readSafe(row, "market_breadth_score", "breadth_score");
        }
        return rise.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal limitEcology(Map<String, Object> row) {
        BigDecimal limitUp = readSafe(row, "limit_up_count", "zt_count");
        BigDecimal limitDown = readSafe(row, "limit_down_count", "dt_count");
        BigDecimal breakBoard = readSafe(row, "break_board_count", "broken_board_count");
        BigDecimal total = limitUp.add(limitDown).add(breakBoard);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return readSafe(row, "limit_ecology_score", "limit_up_ecology_score");
        }
        return limitUp.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal readSafe(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            futureFieldGuardService.assertNoFutureFieldUsed(column);
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }

    private String readStage(Map<String, Object> row) {
        String value = MapFieldReader.string(row, "stage_code");
        if (value == null) value = MapFieldReader.string(row, "stage_type");
        if (value == null) value = MapFieldReader.string(row, "emotion_stage");
        if (value == null) value = MapFieldReader.string(row, "primary_stage");
        return value;
    }

    private Long readId(Map<String, Object> row) {
        Long id = MapFieldReader.longValue(row, "sample_id");
        if (id == null) {
            id = MapFieldReader.longValue(row, "id");
        }
        return id;
    }

    private BigDecimal clamp(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100).setScale(4, RoundingMode.HALF_UP);
        }
        return value.setScale(4, RoundingMode.HALF_UP);
    }
}
