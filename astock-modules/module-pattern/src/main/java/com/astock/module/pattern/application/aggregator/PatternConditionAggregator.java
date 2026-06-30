package com.astock.module.pattern.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.pattern.api.vo.PatternConditionPageVO;
import com.astock.module.pattern.application.query.PatternConditionPageQuery;
import com.astock.module.pattern.domain.repository.PatternConditionPageRepository;
import com.astock.module.pattern.infrastructure.converter.PatternConditionConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class PatternConditionAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final PatternConditionPageRepository pageRepository;
    private final PatternConditionConverter converter;

    public PatternConditionAggregator(DataQualityQueryService dataQualityQueryService,
                       PatternConditionPageRepository pageRepository,
                       PatternConditionConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public PatternConditionPageVO aggregate(PatternConditionPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_09_PATTERN_CONDITION",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_09_PATTERN_CONDITION", "buy_pattern_signal_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
