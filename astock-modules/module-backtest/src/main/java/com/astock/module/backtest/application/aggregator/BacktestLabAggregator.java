package com.astock.module.backtest.application.aggregator;

import com.astock.common.annotation.NoMockAllowed;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.backtest.api.vo.BacktestLabPageVO;
import com.astock.module.backtest.application.query.BacktestLabPageQuery;
import com.astock.module.backtest.domain.repository.BacktestLabPageRepository;
import com.astock.module.backtest.infrastructure.converter.BacktestLabConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoMockAllowed
public class BacktestLabAggregator {
    private final DataQualityQueryService dataQualityQueryService;
    private final BacktestLabPageRepository pageRepository;
    private final BacktestLabConverter converter;

    public BacktestLabAggregator(DataQualityQueryService dataQualityQueryService,
                       BacktestLabPageRepository pageRepository,
                       BacktestLabConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    public BacktestLabPageVO aggregate(BacktestLabPageQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_11_BACKTEST_LAB",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("PAGE_11_BACKTEST_LAB", "backtest_signal_detail", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
