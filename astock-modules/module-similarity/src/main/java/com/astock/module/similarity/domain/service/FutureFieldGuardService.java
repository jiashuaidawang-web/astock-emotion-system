package com.astock.module.similarity.domain.service;

import com.astock.common.exception.FutureLeakageRiskException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class FutureFieldGuardService {
    private static final Set<String> FORBIDDEN_MATCHING_FIELDS = Set.of(
            "future_1d_return",
            "future_3d_return",
            "future_5d_return",
            "future_10d_return",
            "future1d_return",
            "future3d_return",
            "future5d_return",
            "future10d_return",
            "max_drawdown",
            "following_return",
            "following_3d_return",
            "following_5d_return"
    );

    public void assertNoFutureFieldUsed(String fieldName) {
        if (fieldName == null) {
            return;
        }
        String normalized = fieldName.trim().toLowerCase();
        if (FORBIDDEN_MATCHING_FIELDS.contains(normalized)) {
            throw new FutureLeakageRiskException("历史相似度T日匹配禁止使用未来字段：" + fieldName);
        }
    }

    public void assertRowNotUsedDirectlyForMatching(Map<String, Object> row, Iterable<String> selectedFields) {
        if (row == null || selectedFields == null) {
            return;
        }
        for (String field : selectedFields) {
            assertNoFutureFieldUsed(field);
        }
    }
}
