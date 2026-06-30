package com.astock.module.risk.domain.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * RiskControlContext 数据载体。
 */
@Data
public class RiskControlContext {
    /** patternSignalRows 字段。 */
    private List<Map<String, Object>> patternSignalRows;
    /** dataQualityRows 字段。 */
    private List<Map<String, Object>> dataQualityRows;
    /** riskActionMatrixRows 字段。 */
    private List<Map<String, Object>> riskActionMatrixRows;

    public boolean hasPatternSignals() {
        return patternSignalRows != null && !patternSignalRows.isEmpty();
    }

    public boolean hasDataQualityRows() {
        return dataQualityRows != null && !dataQualityRows.isEmpty();
    }

    public boolean hasRiskActionMatrixRows() {
        return riskActionMatrixRows != null && !riskActionMatrixRows.isEmpty();
    }
}
