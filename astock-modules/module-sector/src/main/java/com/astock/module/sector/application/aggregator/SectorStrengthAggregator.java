package com.astock.module.sector.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.sector.api.vo.SectorStrengthPageVO;
import com.astock.module.sector.application.query.SectorStrengthPageQuery;
import com.astock.module.sector.domain.repository.SectorStrengthPageRepository;
import com.astock.module.sector.infrastructure.converter.SectorStrengthConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class SectorStrengthAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final SectorStrengthPageRepository pageRepository;
    private final SectorStrengthConverter converter;

    public SectorStrengthAggregator(DataQualityQueryService dataQualityQueryService,
                       SectorStrengthPageRepository pageRepository,
                       SectorStrengthConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public SectorStrengthPageVO aggregate(SectorStrengthPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_06_SECTOR_STRENGTH",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_06_SECTOR_STRENGTH", "sector_strength_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
