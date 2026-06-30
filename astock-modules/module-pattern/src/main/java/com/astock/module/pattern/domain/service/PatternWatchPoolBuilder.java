package com.astock.module.pattern.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.pattern.domain.model.PatternWatchObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PatternWatchPoolBuilder {

    public List<PatternWatchObject> build(LocalDate tradeDate, String marketScope, PageSnapshotBundle bundle) {
        List<Map<String, Object>> leaderRows = bundle.rows("leader_daily_snapshot");
        List<Map<String, Object>> mainlineRows = bundle.rows("mainline_daily_snapshot");
        Map<String, Object> emotion = bundle.firstRow("emotion_stage_snapshot");
        Map<String, Object> risk = bundle.firstRow("risk_signal_snapshot");

        List<PatternWatchObject> result = new ArrayList<>();
        for (Map<String, Object> leaderRow : leaderRows) {
            String leaderType = readString(leaderRow, "leader_type");
            if (!isAllowedWatchLeaderType(leaderType)) {
                continue;
            }

            PatternWatchObject object = new PatternWatchObject();
            object.setTradeDate(tradeDate);
            object.setMarketScope(marketScope);
            object.setLeaderRow(leaderRow);
            object.setStockCode(readString(leaderRow, "stock_code"));
            object.setStockName(readString(leaderRow, "stock_name"));
            object.setLeaderType(leaderType);
            object.setLeaderStatus(readString(leaderRow, "leader_status"));
            object.setWatchObjectType(mapWatchObjectType(leaderType));
            object.setLeaderScore(readDecimal(leaderRow, "leader_score"));

            object.setMainlineCode(readString(leaderRow, "mainline_code", "theme_code"));
            object.setMainlineName(readString(leaderRow, "mainline_name", "theme_name"));

            Map<String, Object> mainlineRow = findMainlineRow(object, mainlineRows);
            object.setMainlineRow(mainlineRow);
            object.setMainlineStrengthScore(readDecimal(mainlineRow, "mainline_strength_score", "strength_score"));
            object.setEmotionStage(readString(emotion, "primary_stage", "stage_code", "emotion_stage"));
            object.setRiskScore(readDecimal(risk, "risk_score"));
            object.setRiskLevel(readString(risk, "risk_level"));
            object.setRiskVeto(isRiskVeto(risk));
            object.setRiskVetoReason(readString(risk, "risk_action", "risk_text"));
            object.setPatternCalculationAllowed(!"FOLLOWER".equals(leaderType));
            result.add(object);
        }
        return result;
    }

    private boolean isAllowedWatchLeaderType(String leaderType) {
        if (leaderType == null) {
            return false;
        }
        return leaderType.contains("MARKET_LEADER")
                || leaderType.contains("MAINLINE_LEADER")
                || leaderType.contains("TREND_LEADER")
                || leaderType.contains("MIDDLE_ARMY")
                || leaderType.contains("COMPENSATION")
                || leaderType.contains("SWITCH");
    }

    private String mapWatchObjectType(String leaderType) {
        if (leaderType == null) {
            return "UNKNOWN";
        }
        if (leaderType.contains("MARKET_LEADER")) return "MARKET_LEADER";
        if (leaderType.contains("MAINLINE_LEADER")) return "MAINLINE_LEADER";
        if (leaderType.contains("TREND_LEADER")) return "TREND_LEADER";
        if (leaderType.contains("MIDDLE_ARMY")) return "MIDDLE_ARMY";
        if (leaderType.contains("COMPENSATION")) return "COMPENSATION_LEADER";
        if (leaderType.contains("SWITCH")) return "SWITCH_LEADER";
        return "UNKNOWN";
    }

    private Map<String, Object> findMainlineRow(PatternWatchObject object, List<Map<String, Object>> mainlineRows) {
        for (Map<String, Object> row : mainlineRows) {
            String code = readString(row, "mainline_code", "theme_code");
            String name = readString(row, "mainline_name", "theme_name");
            boolean matched = (object.getMainlineCode() != null && object.getMainlineCode().equals(code))
                    || (object.getMainlineName() != null && object.getMainlineName().equals(name));
            if (matched) {
                return row;
            }
        }
        return Map.of();
    }

    private boolean isRiskVeto(Map<String, Object> riskRow) {
        String action = readString(riskRow, "risk_action");
        String level = readString(riskRow, "risk_level");
        return "RISK_VETO".equals(action)
                || "PATTERN_INVALIDATED".equals(action)
                || "EXTREME".equals(level);
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
}
