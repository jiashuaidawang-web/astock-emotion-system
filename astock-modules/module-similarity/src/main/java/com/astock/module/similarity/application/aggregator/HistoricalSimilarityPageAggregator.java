package com.astock.module.similarity.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.similarity.api.vo.HistoricalSimilarityPageVO;
import com.astock.module.similarity.application.query.HistoricalSimilarityPageQuery;
import com.astock.module.similarity.domain.repository.HistoricalSimilarityPageRepository;
import com.astock.module.similarity.infrastructure.converter.HistoricalSimilarityConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class HistoricalSimilarityPageAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final HistoricalSimilarityPageRepository pageRepository;
    private final HistoricalSimilarityConverter converter;

    public HistoricalSimilarityPageAggregator(DataQualityQueryService dataQualityQueryService,
                       HistoricalSimilarityPageRepository pageRepository,
                       HistoricalSimilarityConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public HistoricalSimilarityPageVO aggregate(HistoricalSimilarityPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_02_HISTORICAL_SIMILARITY",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_02_HISTORICAL_SIMILARITY", "market_factor_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
