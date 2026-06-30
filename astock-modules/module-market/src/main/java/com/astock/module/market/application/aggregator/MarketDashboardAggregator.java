package com.astock.module.market.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.market.api.vo.MarketDashboardVO;
import com.astock.module.market.application.query.MarketDashboardQuery;
import com.astock.module.market.domain.repository.MarketDashboardPageRepository;
import com.astock.module.market.infrastructure.converter.MarketDashboardConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class MarketDashboardAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final MarketDashboardPageRepository pageRepository;
    private final MarketDashboardConverter converter;

    public MarketDashboardAggregator(DataQualityQueryService dataQualityQueryService,
                       MarketDashboardPageRepository pageRepository,
                       MarketDashboardConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public MarketDashboardVO aggregate(MarketDashboardQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_01_MARKET_DASHBOARD",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_01_MARKET_DASHBOARD", "market_factor_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
