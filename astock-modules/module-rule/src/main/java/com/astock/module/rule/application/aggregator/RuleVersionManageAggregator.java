package com.astock.module.rule.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.rule.api.vo.RuleVersionManagePageVO;
import com.astock.module.rule.application.query.RuleVersionManagePageQuery;
import com.astock.module.rule.domain.repository.RuleVersionPageRepository;
import com.astock.module.rule.infrastructure.converter.RuleVersionConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class RuleVersionManageAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final RuleVersionPageRepository pageRepository;
    private final RuleVersionConverter converter;

    public RuleVersionManageAggregator(DataQualityQueryService dataQualityQueryService,
                       RuleVersionPageRepository pageRepository,
                       RuleVersionConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public RuleVersionManagePageVO aggregate(RuleVersionManagePageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_14_RULE_VERSION",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_14_RULE_VERSION", "backtest_layer_stat", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
