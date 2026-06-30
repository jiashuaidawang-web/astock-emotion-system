package com.astock.module.backtest.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.backtest.domain.model.BacktestReplaySample;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BacktestReplaySampleBuilder {
    private final BacktestFutureWindowGuardService futureWindowGuardService;

    public BacktestReplaySampleBuilder(BacktestFutureWindowGuardService futureWindowGuardService) {
        this.futureWindowGuardService = futureWindowGuardService;
    }

    public List<BacktestReplaySample> build(LocalDate backtestEndDate, String marketScope, PageSnapshotBundle bundle) {
        List<BacktestReplaySample> samples = new ArrayList<>();
        for (Map<String, Object> row : bundle.rows("historical_cycle_sample")) {
            LocalDate sampleDate = MapFieldReader.localDate(row, "trade_date");
            if (sampleDate == null || !sampleDate.isBefore(backtestEndDate)) {
                continue;
            }

            BacktestReplaySample sample = new BacktestReplaySample();
            sample.setSampleRow(row);
            sample.setSampleId(readLong(row, "sample_id", "id"));
            sample.setSampleDate(sampleDate);
            sample.setMarketScope(marketScope);
            sample.setStageCode(readString(row, "stage_code", "emotion_stage", "primary_stage"));
            sample.setPatternCode(readStringNoFuture(row, "pattern_code"));
            sample.setStockCode(readStringNoFuture(row, "stock_code"));
            sample.setStockName(readStringNoFuture(row, "stock_name"));
            sample.setMainlineCode(readStringNoFuture(row, "mainline_code", "theme_code"));
            sample.setMainlineName(readStringNoFuture(row, "mainline_name", "theme_name"));
            sample.setSignalRow(findPatternSignal(sample, bundle.rows("buy_pattern_signal_snapshot")));
            sample.setRiskRow(findRiskSignal(sample, bundle.rows("risk_signal_snapshot")));
            sample.setSignalScore(readDecimalNoFuture(sample.getSignalRow(), "condition_score", "pattern_condition_score"));
            sample.setRiskScore(readDecimalNoFuture(sample.getRiskRow(), "risk_score"));
            sample.setRiskAction(readStringNoFuture(sample.getRiskRow(), "risk_action"));
            attachFutureWindowReturns(backtestEndDate, sample, row);
            samples.add(sample);
        }
        return samples;
    }

    private void attachFutureWindowReturns(LocalDate backtestEndDate, BacktestReplaySample sample, Map<String, Object> row) {
        futureWindowGuardService.assertFutureReadAllowed(sample.getSampleDate(), backtestEndDate, "future_1d_return");
        futureWindowGuardService.assertFutureReadAllowed(sample.getSampleDate(), backtestEndDate, "future_3d_return");
        futureWindowGuardService.assertFutureReadAllowed(sample.getSampleDate(), backtestEndDate, "future_5d_return");
        futureWindowGuardService.assertFutureReadAllowed(sample.getSampleDate(), backtestEndDate, "future_10d_return");
        futureWindowGuardService.assertFutureReadAllowed(sample.getSampleDate(), backtestEndDate, "max_drawdown");

        sample.setFuture1dReturn(readDecimal(row, "future_1d_return", "future1d_return"));
        sample.setFuture3dReturn(readDecimal(row, "future_3d_return", "future3d_return"));
        sample.setFuture5dReturn(readDecimal(row, "future_5d_return", "future5d_return"));
        sample.setFuture10dReturn(readDecimal(row, "future_10d_return", "future10d_return"));
        sample.setMaxDrawdown(readDecimal(row, "max_drawdown"));
    }

    private Map<String, Object> findPatternSignal(BacktestReplaySample sample, List<Map<String, Object>> signalRows) {
        for (Map<String, Object> row : signalRows) {
            LocalDate signalDate = MapFieldReader.localDate(row, "trade_date");
            String stockCode = MapFieldReader.string(row, "stock_code");
            String patternCode = MapFieldReader.string(row, "pattern_code");
            boolean dateMatched = sample.getSampleDate() != null && sample.getSampleDate().equals(signalDate);
            boolean stockMatched = sample.getStockCode() == null || sample.getStockCode().equals(stockCode);
            boolean patternMatched = sample.getPatternCode() == null || sample.getPatternCode().equals(patternCode);
            if (dateMatched && stockMatched && patternMatched) {
                return row;
            }
        }
        return Map.of();
    }

    private Map<String, Object> findRiskSignal(BacktestReplaySample sample, List<Map<String, Object>> riskRows) {
        for (Map<String, Object> row : riskRows) {
            LocalDate riskDate = MapFieldReader.localDate(row, "trade_date");
            if (sample.getSampleDate() != null && sample.getSampleDate().equals(riskDate)) {
                return row;
            }
        }
        return Map.of();
    }

    private String readStringNoFuture(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            futureWindowGuardService.assertNotFutureFieldForSignalReplay(column);
            String value = MapFieldReader.string(row, column);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private BigDecimal readDecimalNoFuture(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            futureWindowGuardService.assertNotFutureFieldForSignalReplay(column);
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                return value;
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

    private Long readLong(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            Long value = MapFieldReader.longValue(row, column);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
