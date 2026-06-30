package com.astock.module.leader.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.leader.api.vo.LeaderLadderPageVO;
import com.astock.module.leader.application.query.LeaderLadderPageQuery;
import com.astock.module.leader.domain.repository.LeaderLadderPageRepository;
import com.astock.module.leader.infrastructure.converter.LeaderLadderConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class LeaderLadderAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final LeaderLadderPageRepository pageRepository;
    private final LeaderLadderConverter converter;

    public LeaderLadderAggregator(DataQualityQueryService dataQualityQueryService,
                       LeaderLadderPageRepository pageRepository,
                       LeaderLadderConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public LeaderLadderPageVO aggregate(LeaderLadderPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_07_LEADER_LADDER",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_07_LEADER_LADDER", "leader_daily_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
