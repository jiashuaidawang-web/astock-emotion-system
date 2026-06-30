package com.astock.module.risk.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.risk.api.vo.RiskControlPageVO;
import com.astock.module.risk.application.query.RiskControlPageQuery;
import com.astock.module.risk.domain.repository.RiskControlPageRepository;
import com.astock.module.risk.infrastructure.converter.RiskControlConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class RiskControlAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final RiskControlPageRepository pageRepository;
    private final RiskControlConverter converter;

    public RiskControlAggregator(DataQualityQueryService dataQualityQueryService,
                       RiskControlPageRepository pageRepository,
                       RiskControlConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public RiskControlPageVO aggregate(RiskControlPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_10_RISK_CONTROL",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_10_RISK_CONTROL", "risk_signal_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
