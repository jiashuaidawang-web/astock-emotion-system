package com.astock.module.risk.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.risk.domain.model.RiskControlContext;
import com.astock.module.risk.domain.model.RiskFactorSnapshot;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RiskFactorExtractor {

    public RiskFactorSnapshot extract(LocalDate tradeDate,
                                      String marketScope,
                                      PageSnapshotBundle bundle,
                                      RiskControlContext context) {
        Map<String, Object> emotion = bundle.firstRow("emotion_stage_snapshot");
        Map<String, Object> market = bundle.firstRow("market_factor_snapshot");
        Map<String, Object> limitEco = bundle.firstRow("limit_up_down_ecology_snapshot");

        RiskFactorSnapshot snapshot = new RiskFactorSnapshot();
        snapshot.setTradeDate(tradeDate);
        snapshot.setMarketScope(marketScope);
        snapshot.setEmotionRow(emotion);
        snapshot.setMarketRow(market);
        snapshot.setLimitEcoRow(limitEco);
        snapshot.setEmotionStage(readString(emotion, "primary_stage", "stage_code", "emotion_stage"));
        snapshot.setEmotionStageConfidence(readDecimal(emotion, "stage_confidence", "stage_score"));
        snapshot.setLossEffectScore(readDecimal(market, "loss_effect_score", "loss_pressure_score"));
        snapshot.setLimitDownPressureScore(limitDownPressure(limitEco));
        snapshot.setBreakBoardPressureScore(breakBoardPressure(limitEco));
        snapshot.setLeaderNegativeFeedbackScore(avgScore(bundle.rows("leader_negative_feedback"), "negative_feedback_score"));
        snapshot.setMainlineDecayScore(mainlineDecayScore(bundle.rows("mainline_daily_snapshot")));
        snapshot.setIndexFundRiskScore(readDecimal(market, "index_fund_risk_score", "index_pressure_score"));
        snapshot.setDataIntegrityRiskScore(dataIntegrityRiskScore(context));
        return snapshot;
    }

    private BigDecimal limitDownPressure(Map<String, Object> row) {
        BigDecimal limitDown = readDecimal(row, "limit_down_count", "dt_count");
        BigDecimal limitUp = readDecimal(row, "limit_up_count", "zt_count");
        BigDecimal broken = readDecimal(row, "break_board_count", "broken_board_count");
        BigDecimal total = limitDown.add(limitUp).add(broken);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return readDecimal(row, "limit_down_pressure_score");
        }
        return clamp(limitDown.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP));
    }

    private BigDecimal breakBoardPressure(Map<String, Object> row) {
        BigDecimal limitDown = readDecimal(row, "limit_down_count", "dt_count");
        BigDecimal limitUp = readDecimal(row, "limit_up_count", "zt_count");
        BigDecimal broken = readDecimal(row, "break_board_count", "broken_board_count");
        BigDecimal total = limitDown.add(limitUp).add(broken);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return readDecimal(row, "break_board_pressure_score");
        }
        return clamp(broken.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP));
    }

    private BigDecimal mainlineDecayScore(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : rows) {
            BigDecimal strength = readDecimal(row, "mainline_strength_score", "strength_score");
            BigDecimal risk = readDecimal(row, "mainline_decay_risk_score", "risk_score");
            BigDecimal one = risk.compareTo(BigDecimal.ZERO) > 0
                    ? risk
                    : BigDecimal.valueOf(100).subtract(strength);
            total = total.add(clamp(one));
            count++;
        }
        return total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal dataIntegrityRiskScore(RiskControlContext context) {
        if (context == null || !context.hasDataQualityRows()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : context.getDataQualityRows()) {
            BigDecimal ratio = readDecimal(row, "completeness_ratio");
            Boolean critical = MapFieldReader.bool(row, "critical");
            BigDecimal one = BigDecimal.valueOf(100).subtract(ratio.multiply(BigDecimal.valueOf(100)));
            if (Boolean.TRUE.equals(critical)) {
                one = one.add(BigDecimal.valueOf(20));
            }
            total = total.add(clamp(one));
            count++;
        }
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal avgScore(List<Map<String, Object>> rows, String column) {
        if (rows == null || rows.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : rows) {
            total = total.add(readDecimal(row, column));
            count++;
        }
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }

    private String readString(Map<String, Object> row, String... columns) {
        if (row == null) {
            return null;
        }
        for (String column : columns) {
            String value = MapFieldReader.string(row, column);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private BigDecimal readDecimal(Map<String, Object> row, String... columns) {
        if (row == null) {
            return BigDecimal.ZERO;
        }
        for (String column : columns) {
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                return value;
            }
        }
        return BigDecimal.ZERO;
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
