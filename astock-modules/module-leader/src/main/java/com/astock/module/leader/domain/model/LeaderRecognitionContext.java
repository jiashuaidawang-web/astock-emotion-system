package com.astock.module.leader.domain.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * LeaderRecognitionContext 数据载体。
 */
@Data
public class LeaderRecognitionContext {
    /** previousLeaderRows 字段。 */
    private List<Map<String, Object>> previousLeaderRows;
    /** riskRows 字段。 */
    private List<Map<String, Object>> riskRows;
    /** patternRows 字段。 */
    private List<Map<String, Object>> patternRows;

    public boolean hasPreviousLeaderRows() {
        return previousLeaderRows != null && !previousLeaderRows.isEmpty();
    }

    public boolean hasRiskRows() {
        return riskRows != null && !riskRows.isEmpty();
    }

    public boolean hasPatternRows() {
        return patternRows != null && !patternRows.isEmpty();
    }
}
