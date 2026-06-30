package com.astock.module.sample.application.service;

import com.astock.module.sample.api.vo.HistoricalCycleSamplePageVO;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;

public interface HistoricalCycleSampleQueryService {
    HistoricalCycleSamplePageVO querySamplePage(HistoricalCycleSamplePageQuery query);
}
