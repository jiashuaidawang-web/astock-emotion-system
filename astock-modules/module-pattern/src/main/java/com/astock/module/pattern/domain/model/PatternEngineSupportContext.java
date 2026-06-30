package com.astock.module.pattern.domain.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * PatternEngineSupportContext 数据载体。
 */
@Data
public class PatternEngineSupportContext {
    /** patternBacktestRows 字段。 */
    private List<Map<String, Object>> patternBacktestRows;
    /** riskBindingRows 字段。 */
    private List<Map<String, Object>> riskBindingRows;
    /** stageMatrixRows 字段。 */
    private List<Map<String, Object>> stageMatrixRows;
    /** ruleConfigRows 字段。 */
    private List<Map<String, Object>> ruleConfigRows;

    public boolean hasPatternBacktestRows() {
        return patternBacktestRows != null && !patternBacktestRows.isEmpty();
    }

    public boolean hasRiskBindingRows() {
        return riskBindingRows != null && !riskBindingRows.isEmpty();
    }

    public boolean hasStageMatrixRows() {
        return stageMatrixRows != null && !stageMatrixRows.isEmpty();
    }
}
