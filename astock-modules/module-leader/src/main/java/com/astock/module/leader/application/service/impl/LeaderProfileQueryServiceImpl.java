package com.astock.module.leader.application.service.impl;

import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.leader.api.vo.LeaderProfilePageVO;
import com.astock.module.leader.application.query.LeaderProfilePageQuery;
import com.astock.module.leader.application.service.LeaderProfileQueryService;
import com.astock.module.leader.domain.repository.LeaderProfilePageRepository;
import com.astock.module.leader.infrastructure.converter.LeaderProfileConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaderProfileQueryServiceImpl implements LeaderProfileQueryService {
    private final DataQualityQueryService dataQualityQueryService;
    private final LeaderProfilePageRepository pageRepository;
    private final LeaderProfileConverter converter;

    public LeaderProfileQueryServiceImpl(DataQualityQueryService dataQualityQueryService,
                                         LeaderProfilePageRepository pageRepository,
                                         LeaderProfileConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    @Override
    public LeaderProfilePageVO queryProfile(LeaderProfilePageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_08_LEADER_PROFILE",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("LEADER", "leader_daily_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
