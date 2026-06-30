package com.astock.module.emotion.domain.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * EmotionHistoricalContext 数据载体。
 */
@Data
public class EmotionHistoricalContext {
    /** historicalCycleSamples 字段。 */
    private List<Map<String, Object>> historicalCycleSamples;
    /** cycleSampleConfirms 字段。 */
    private List<Map<String, Object>> cycleSampleConfirms;
    /** manualStageAdjustments 字段。 */
    private List<Map<String, Object>> manualStageAdjustments;
    /** recentStageTransitions 字段。 */
    private List<Map<String, Object>> recentStageTransitions;

    public boolean hasHistoricalSamples() {
        return historicalCycleSamples != null && !historicalCycleSamples.isEmpty();
    }

    public boolean hasConfirmedSamples() {
        return cycleSampleConfirms != null && !cycleSampleConfirms.isEmpty();
    }

    public boolean hasManualAdjustments() {
        return manualStageAdjustments != null && !manualStageAdjustments.isEmpty();
    }

    public boolean hasRecentTransitions() {
        return recentStageTransitions != null && !recentStageTransitions.isEmpty();
    }
}
