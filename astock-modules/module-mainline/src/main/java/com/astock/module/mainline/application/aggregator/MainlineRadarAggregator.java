package com.astock.module.mainline.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.mainline.api.vo.MainlineRadarPageVO;
import com.astock.module.mainline.application.query.MainlineRadarPageQuery;
import com.astock.module.mainline.domain.repository.MainlineRadarPageRepository;
import com.astock.module.mainline.infrastructure.converter.MainlineRadarConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class MainlineRadarAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final MainlineRadarPageRepository pageRepository;
    private final MainlineRadarConverter converter;

    public MainlineRadarAggregator(DataQualityQueryService dataQualityQueryService,
                       MainlineRadarPageRepository pageRepository,
                       MainlineRadarConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public MainlineRadarPageVO aggregate(MainlineRadarPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_05_MAINLINE_RADAR",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_05_MAINLINE_RADAR", "mainline_daily_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
