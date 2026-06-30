package com.astock.module.leader.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.leader.domain.model.LeaderCandidateFeature;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LeaderFeatureExtractor {

    public List<LeaderCandidateFeature> extract(LocalDate tradeDate, String marketScope, PageSnapshotBundle bundle) {
        List<Map<String, Object>> stockRows = bundle.rows("stock_daily_kline");
        List<Map<String, Object>> mainlineRows = bundle.rows("mainline_daily_snapshot");
        List<Map<String, Object>> sectorRows = bundle.rows("sector_strength_snapshot");
        Map<String, Object> limitEco = bundle.firstRow("limit_up_down_ecology_snapshot");

        BigDecimal marketMaxBoardHeight = readDecimal(limitEco, "max_board_height", "highest_board_height");

        List<LeaderCandidateFeature> features = new ArrayList<>();
        for (Map<String, Object> row : stockRows) {
            String stockCode = readString(row, "stock_code", "code");
            String stockName = readString(row, "stock_name", "name");
            if (stockCode == null || stockCode.isBlank()) {
                continue;
            }

            LeaderCandidateFeature feature = new LeaderCandidateFeature();
            feature.setTradeDate(tradeDate);
            feature.setMarketScope(marketScope);
            feature.setSourceRow(row);
            feature.setStockCode(stockCode);
            feature.setStockName(stockName);
            feature.setSectorCode(readString(row, "sector_code", "industry_code", "theme_code"));
            feature.setSectorName(readString(row, "sector_name", "industry_name", "theme_name"));
            feature.setMainlineCode(readString(row, "mainline_code", "theme_code"));
            feature.setMainlineName(readString(row, "mainline_name", "theme_name"));
            feature.setPctChange(readDecimal(row, "pct_change", "change_pct"));
            feature.setTurnoverAmount(readDecimal(row, "turnover_amount", "amount"));
            feature.setTurnoverRate(readDecimal(row, "turnover_rate"));
            feature.setVolumeRatio(readDecimal(row, "volume_ratio"));
            feature.setBoardHeight(readDecimal(row, "board_height", "consecutive_board_height", "limit_up_days"));
            feature.setMaxBoardHeight(marketMaxBoardHeight);
            feature.setLimitUp(readBool(row, "limit_up", "is_limit_up"));
            feature.setBrokenBoard(readBool(row, "broken_board", "is_broken_board"));
            feature.setMainlineStrengthScore(resolveMainlineStrength(feature, mainlineRows));
            feature.setSectorStrengthScore(resolveSectorStrength(feature, sectorRows));
            feature.setNegativeRawScore(readDecimal(row, "negative_feedback_score", "drawdown_score"));
            features.add(feature);
        }
        return features;
    }

    private BigDecimal resolveMainlineStrength(LeaderCandidateFeature feature, List<Map<String, Object>> mainlineRows) {
        for (Map<String, Object> row : mainlineRows) {
            String code = readString(row, "mainline_code", "theme_code");
            String name = readString(row, "mainline_name", "theme_name");
            boolean matched = (feature.getMainlineCode() != null && feature.getMainlineCode().equals(code))
                    || (feature.getMainlineName() != null && feature.getMainlineName().equals(name))
                    || (feature.getSectorName() != null && feature.getSectorName().equals(name));
            if (matched) {
                return readDecimal(row, "mainline_strength_score", "strength_score");
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal resolveSectorStrength(LeaderCandidateFeature feature, List<Map<String, Object>> sectorRows) {
        for (Map<String, Object> row : sectorRows) {
            String code = readString(row, "sector_code", "theme_code");
            String name = readString(row, "sector_name", "theme_name");
            boolean matched = (feature.getSectorCode() != null && feature.getSectorCode().equals(code))
                    || (feature.getSectorName() != null && feature.getSectorName().equals(name));
            if (matched) {
                return readDecimal(row, "sector_strength_score", "strength_score");
            }
        }
        return BigDecimal.ZERO;
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

    private Boolean readBool(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            Boolean value = MapFieldReader.bool(row, column);
            if (value != null) {
                return value;
            }
        }
        return false;
    }
}
