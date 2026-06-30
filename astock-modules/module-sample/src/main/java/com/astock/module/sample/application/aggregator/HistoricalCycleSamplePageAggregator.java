package com.astock.module.sample.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.sample.api.vo.HistoricalCycleSamplePageVO;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;
import com.astock.module.sample.domain.repository.HistoricalCycleSamplePageRepository;
import com.astock.module.sample.infrastructure.converter.HistoricalCycleSampleConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class HistoricalCycleSamplePageAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final HistoricalCycleSamplePageRepository pageRepository;
    private final HistoricalCycleSampleConverter converter;

    public HistoricalCycleSamplePageAggregator(DataQualityQueryService dataQualityQueryService,
                       HistoricalCycleSamplePageRepository pageRepository,
                       HistoricalCycleSampleConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public HistoricalCycleSamplePageVO aggregate(HistoricalCycleSamplePageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_04_CYCLE_SAMPLE_LIBRARY",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_04_CYCLE_SAMPLE_LIBRARY", "historical_cycle_sample", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
