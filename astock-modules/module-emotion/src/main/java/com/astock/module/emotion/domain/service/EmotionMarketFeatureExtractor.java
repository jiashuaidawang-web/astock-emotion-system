package com.astock.module.emotion.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.emotion.domain.model.EmotionMarketFeature;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
public class EmotionMarketFeatureExtractor {

    public EmotionMarketFeature extract(LocalDate tradeDate, String marketScope, PageSnapshotBundle bundle) {
        Map<String, Object> market = bundle.firstRow("market_factor_snapshot");
        Map<String, Object> limitEco = bundle.firstRow("limit_up_down_ecology_snapshot");

        EmotionMarketFeature feature = new EmotionMarketFeature();
        feature.setTradeDate(tradeDate);
        feature.setMarketScope(marketScope);
        feature.setMarketRow(market);
        feature.setLimitEcoRow(limitEco);

        BigDecimal riseCount = readAny(market, "rise_count", "up_count", "rising_count");
        BigDecimal fallCount = readAny(market, "fall_count", "down_count", "falling_count");
        BigDecimal flatCount = readAny(market, "flat_count", "unchanged_count");
        BigDecimal limitUpCount = readAny(limitEco, "limit_up_count", "zt_count");
        BigDecimal limitDownCount = readAny(limitEco, "limit_down_count", "dt_count");
        BigDecimal breakBoardCount = readAny(limitEco, "break_board_count", "broken_board_count");
        BigDecimal maxBoardHeight = readAny(limitEco, "max_board_height", "highest_board_height");
        BigDecimal turnoverPercentile = readAny(market, "turnover_percentile", "amount_percentile");
        BigDecimal profitEffect = readAny(market, "profit_effect_score", "earning_effect_score");
        BigDecimal lossEffect = readAny(market, "loss_effect_score", "loss_pressure_score");

        BigDecimal total = riseCount.add(fallCount).add(flatCount);
        BigDecimal breadthScore = total.compareTo(BigDecimal.ZERO) <= 0
                ? readAny(market, "market_breadth_score", "breadth_score")
                : riseCount.multiply(BigDecimal.valueOf(100)).divide(total, 4, RoundingMode.HALF_UP);

        BigDecimal limitTotal = limitUpCount.add(limitDownCount).add(breakBoardCount);
        BigDecimal limitUpEcoScore = limitTotal.compareTo(BigDecimal.ZERO) <= 0
                ? readAny(limitEco, "limit_up_ecology_score", "limit_eco_score")
                : limitUpCount.multiply(BigDecimal.valueOf(100)).divide(limitTotal, 4, RoundingMode.HALF_UP);

        BigDecimal limitDownPressure = limitTotal.compareTo(BigDecimal.ZERO) <= 0
                ? readAny(limitEco, "limit_down_pressure_score")
                : limitDownCount.multiply(BigDecimal.valueOf(100)).divide(limitTotal, 4, RoundingMode.HALF_UP);

        BigDecimal breakPressure = limitTotal.compareTo(BigDecimal.ZERO) <= 0
                ? readAny(limitEco, "break_board_pressure_score")
                : breakBoardCount.multiply(BigDecimal.valueOf(100)).divide(limitTotal, 4, RoundingMode.HALF_UP);

        BigDecimal ladderHeightScore = clamp(maxBoardHeight.multiply(BigDecimal.valueOf(10)));

        feature.setMarketBreadthScore(clamp(breadthScore));
        feature.setProfitEffectScore(clamp(profitEffect));
        feature.setLossEffectScore(clamp(lossEffect));
        feature.setLimitUpEcoScore(clamp(limitUpEcoScore));
        feature.setLimitDownPressureScore(clamp(limitDownPressure));
        feature.setLadderHeightScore(clamp(ladderHeightScore));
        feature.setBreakBoardPressureScore(clamp(breakPressure));
        feature.setTurnoverHeatScore(clamp(turnoverPercentile));
        return feature;
    }

    private BigDecimal readAny(Map<String, Object> row, String... columns) {
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
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }
        return value.setScale(4, RoundingMode.HALF_UP);
    }
}
