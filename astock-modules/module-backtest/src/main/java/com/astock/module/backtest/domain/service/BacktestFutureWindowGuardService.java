package com.astock.module.backtest.domain.service;

import com.astock.common.exception.FutureLeakageRiskException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class BacktestFutureWindowGuardService {
    private static final Set<String> FUTURE_FIELDS = Set.of(
            "future_1d_return",
            "future_3d_return",
            "future_5d_return",
            "future_10d_return",
            "future1d_return",
            "future3d_return",
            "future5d_return",
            "future10d_return",
            "max_drawdown"
    );

    public void assertNotFutureFieldForSignalReplay(String fieldName) {
        if (fieldName == null) {
            return;
        }
        if (FUTURE_FIELDS.contains(fieldName.toLowerCase())) {
            throw new FutureLeakageRiskException("信号回放阶段禁止读取future字段：" + fieldName);
        }
    }

    public void assertFutureReadAllowed(LocalDate sampleDate, LocalDate backtestEndDate, String fieldName) {
        if (fieldName == null || !FUTURE_FIELDS.contains(fieldName.toLowerCase())) {
            return;
        }
        if (sampleDate == null || backtestEndDate == null || !sampleDate.isBefore(backtestEndDate)) {
            throw new FutureLeakageRiskException("future字段只能在历史样本回测窗口读取：" + fieldName);
        }
    }
}
