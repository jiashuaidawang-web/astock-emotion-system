package com.astock.infrastructure.engine;

import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.RequiredSnapshot;
import com.astock.common.exception.BusinessException;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EngineDataQualityGuard {
    private final DataQualityQueryService dataQualityQueryService;

    public EngineDataQualityGuard(DataQualityQueryService dataQualityQueryService) {
        this.dataQualityQueryService = dataQualityQueryService;
    }

    public PageDataQualityVO check(EngineRunCommand command) {
        List<RequiredSnapshot> snapshots = command.getInputTables().stream()
                .map(table -> new RequiredSnapshot(table.toUpperCase(), table, true))
                .toList();
        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                command.getTaskType(),
                command.getTradeDate(),
                command.getMarketScope(),
                snapshots
        );
        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            throw new BusinessException("DATA_INCOMPLETE", quality.getDataStatusText());
        }
        return quality;
    }
}
