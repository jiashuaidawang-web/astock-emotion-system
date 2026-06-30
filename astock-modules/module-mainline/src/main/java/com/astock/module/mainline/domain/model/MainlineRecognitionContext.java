package com.astock.module.mainline.domain.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * MainlineRecognitionContext 数据载体。
 */
@Data
public class MainlineRecognitionContext {
    /** sectorRows 字段。 */
    private List<Map<String, Object>> sectorRows;
    /** leaderRows 字段。 */
    private List<Map<String, Object>> leaderRows;
    /** previousMainlineRows 字段。 */
    private List<Map<String, Object>> previousMainlineRows;

    public boolean hasLeaderRows() {
        return leaderRows != null && !leaderRows.isEmpty();
    }

    public boolean hasSectorRows() {
        return sectorRows != null && !sectorRows.isEmpty();
    }

    public boolean hasPreviousMainlineRows() {
        return previousMainlineRows != null && !previousMainlineRows.isEmpty();
    }
}
