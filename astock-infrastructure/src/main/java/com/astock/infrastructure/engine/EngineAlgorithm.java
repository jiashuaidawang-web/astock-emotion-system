package com.astock.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface EngineAlgorithm {
    Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                   Long resolvedRuleVersionId,
                                                   PageSnapshotBundle inputBundle);
}
