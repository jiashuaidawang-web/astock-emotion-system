package com.astock.module.backtest.application.service.impl;

import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.DataQualityQueryService;
import com.astock.module.backtest.api.vo.BacktestReportDetailVO;
import com.astock.module.backtest.application.query.BacktestReportDetailQuery;
import com.astock.module.backtest.application.service.BacktestReportQueryService;
import com.astock.module.backtest.domain.repository.BacktestReportPageRepository;
import com.astock.module.backtest.infrastructure.converter.BacktestReportConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BacktestReportQueryServiceImpl implements BacktestReportQueryService {
    private final DataQualityQueryService dataQualityQueryService;
    private final BacktestReportPageRepository pageRepository;
    private final BacktestReportConverter converter;

    public BacktestReportQueryServiceImpl(DataQualityQueryService dataQualityQueryService,
                                          BacktestReportPageRepository pageRepository,
                                          BacktestReportConverter converter) {
        this.dataQualityQueryService = dataQualityQueryService;
        this.pageRepository = pageRepository;
        this.converter = converter;
    }

    @Override
    public BacktestReportDetailVO queryReportDetail(BacktestReportDetailQuery query) {
        LocalDate tradeDate = query.getTradeDate() == null ? LocalDate.now() : query.getTradeDate();
        String marketScope = query.getMarketScope() == null ? "A_SHARE" : query.getMarketScope();
        query.setTradeDate(tradeDate);
        query.setMarketScope(marketScope);

        PageDataQualityVO quality = dataQualityQueryService.checkPage(
                "PAGE_12_BACKTEST_REPORT",
                tradeDate,
                marketScope,
                List.of(new RequiredSnapshot("BACKTEST_LAYER", "backtest_layer_stat", true))
        );

        if (!Boolean.TRUE.equals(quality.getDataComplete())) {
            return converter.convert(query, quality, new PageSnapshotBundle());
        }

        PageSnapshotBundle bundle = pageRepository.queryPage(tradeDate, marketScope, 50);
        return converter.convert(query, quality, bundle);
    }
}
