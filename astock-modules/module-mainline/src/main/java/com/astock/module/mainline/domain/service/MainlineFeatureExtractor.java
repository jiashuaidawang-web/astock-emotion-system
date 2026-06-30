package com.astock.module.mainline.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.mainline.domain.model.MainlineRecognitionContext;
import com.astock.module.mainline.domain.model.MainlineThemeFeature;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MainlineFeatureExtractor {

    public List<MainlineThemeFeature> extract(LocalDate tradeDate,
                                              String marketScope,
                                              PageSnapshotBundle bundle,
                                              MainlineRecognitionContext context) {
        List<Map<String, Object>> themeRows = bundle.rows("theme_daily_snapshot");
        if (themeRows.isEmpty()) {
            themeRows = bundle.rows("sector_strength_snapshot");
        }
        if (themeRows.isEmpty() && context != null && context.hasSectorRows()) {
            themeRows = context.getSectorRows();
        }

        String emotionStage = readEmotionStage(bundle.firstRow("emotion_stage_snapshot"));

        List<MainlineThemeFeature> features = new ArrayList<>();
        for (Map<String, Object> row : themeRows) {
            MainlineThemeFeature feature = new MainlineThemeFeature();
            feature.setTradeDate(tradeDate);
            feature.setMarketScope(marketScope);
            feature.setSourceRow(row);
            feature.setThemeCode(readString(row, "theme_code", "sector_code", "mainline_code"));
            feature.setThemeName(readString(row, "theme_name", "sector_name", "mainline_name"));
            feature.setThemeType(readString(row, "theme_type", "sector_type"));
            feature.setPctChange(readDecimal(row, "pct_change", "change_pct"));
            feature.setLimitUpCount(readDecimal(row, "limit_up_count", "zt_count"));
            feature.setStockCount(readDecimal(row, "stock_count", "component_count"));
            feature.setTurnoverAmount(readDecimal(row, "turnover_amount", "amount"));
            feature.setTurnoverRatio(readDecimal(row, "turnover_ratio", "turnover_amount_ratio"));
            feature.setContinuityDays(readDecimal(row, "continuity_days", "continuous_days", "active_days"));
            feature.setMaxBoardHeight(readDecimal(row, "max_board_height", "highest_board_height"));
            feature.setLeaderCount(readDecimal(row, "leader_count", "core_stock_count"));
            feature.setLeaderDriveRawScore(resolveLeaderDriveScore(feature.getThemeCode(), feature.getThemeName(), context));
            feature.setEmotionStage(emotionStage);
            if (feature.getThemeCode() != null || feature.getThemeName() != null) {
                features.add(feature);
            }
        }
        return features;
    }

    private BigDecimal resolveLeaderDriveScore(String themeCode, String themeName, MainlineRecognitionContext context) {
        if (context == null || !context.hasLeaderRows()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : context.getLeaderRows()) {
            String rowThemeCode = readString(row, "theme_code", "mainline_code", "sector_code");
            String rowThemeName = readString(row, "theme_name", "mainline_name", "sector_name");
            boolean matched = (themeCode != null && themeCode.equals(rowThemeCode))
                    || (themeName != null && themeName.equals(rowThemeName));
            if (!matched) {
                continue;
            }
            BigDecimal score = readDecimal(row, "leader_drive_score", "drive_score", "leader_score");
            total = total.add(score);
            count++;
        }
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 4, java.math.RoundingMode.HALF_UP);
    }

    private String readEmotionStage(Map<String, Object> row) {
        String value = MapFieldReader.string(row, "primary_stage");
        if (value == null) value = MapFieldReader.string(row, "stage_code");
        if (value == null) value = MapFieldReader.string(row, "emotion_stage");
        return value == null ? "UNKNOWN" : value;
    }

    private String readString(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            String value = MapFieldReader.string(row, column);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private BigDecimal readDecimal(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }
}
