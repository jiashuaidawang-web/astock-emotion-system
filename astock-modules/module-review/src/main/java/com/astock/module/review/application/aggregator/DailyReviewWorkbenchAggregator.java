package com.astock.module.review.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.review.api.vo.DailyReviewWorkbenchVO;
import com.astock.module.review.application.query.DailyReviewWorkbenchQuery;
import com.astock.module.review.domain.repository.DailyReviewPageRepository;
import com.astock.module.review.infrastructure.converter.DailyReviewConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class DailyReviewWorkbenchAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final DailyReviewPageRepository pageRepository;
    private final DailyReviewConverter converter;

    public DailyReviewWorkbenchAggregator(DataQualityQueryService dataQualityQueryService,
                       DailyReviewPageRepository pageRepository,
                       DailyReviewConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public DailyReviewWorkbenchVO aggregate(DailyReviewWorkbenchQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_13_DAILY_REVIEW",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_13_DAILY_REVIEW", "market_factor_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
