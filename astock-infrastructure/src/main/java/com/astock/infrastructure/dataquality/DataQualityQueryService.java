package com.astock.infrastructure.dataquality;

import com.astock.common.data.DataQualityCheckVO;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.RequiredSnapshot;
import java.time.LocalDate;
import java.util.List;

public interface DataQualityQueryService {
    PageDataQualityVO checkPage(String pageCode, LocalDate tradeDate, String marketScope, List<RequiredSnapshot> requiredSnapshots);
    List<DataQualityCheckVO> queryPersistedChecks(LocalDate tradeDate);
}
