package com.astock.module.spider.service;

import com.astock.module.spider.domain.entity.StockDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDimensionRow;
import com.astock.module.spider.domain.entity.StockPlateRelationRow;
import com.astock.module.spider.domain.entity.StockPoolDailySnapshotRow;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SpiderClickHouseRepository {

    int insertStockDailyKline(List<StockDailyKlineRow> rows);

    int insertPlateDimensions(List<StockPlateDimensionRow> rows);

    int insertPlateDailyKline(List<StockPlateDailyKlineRow> rows);

    int insertPlateRelations(List<StockPlateRelationRow> rows);

    int insertPoolSnapshots(List<StockPoolDailySnapshotRow> rows);

    Map<String, Object> validateDaily(LocalDate tradeDate);
}
