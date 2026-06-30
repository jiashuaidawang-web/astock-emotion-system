package com.astock.module.emotion.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.emotion.api.vo.EmotionCycleStateMachineVO;
import com.astock.module.emotion.application.query.EmotionCycleStateMachineQuery;
import com.astock.module.emotion.domain.repository.EmotionStateMachinePageRepository;
import com.astock.module.emotion.infrastructure.converter.EmotionStateMachineConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class EmotionStateMachineAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final EmotionStateMachinePageRepository pageRepository;
    private final EmotionStateMachineConverter converter;

    public EmotionStateMachineAggregator(DataQualityQueryService dataQualityQueryService,
                       EmotionStateMachinePageRepository pageRepository,
                       EmotionStateMachineConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public EmotionCycleStateMachineVO aggregate(EmotionCycleStateMachineQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_03_EMOTION_STATE_MACHINE",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_03_EMOTION_STATE_MACHINE", "emotion_stage_snapshot", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
