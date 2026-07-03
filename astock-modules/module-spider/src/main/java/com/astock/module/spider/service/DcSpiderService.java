package com.astock.module.spider.service;

import java.time.LocalDate;
import java.util.Map;

public interface DcSpiderService {

    Map<String, Object> syncAll(LocalDate tradeDate);

    int syncStockDailyKline(LocalDate tradeDate);

    int syncPlates(LocalDate tradeDate);

    int syncPlateRelations(LocalDate tradeDate);

    int syncPools(LocalDate tradeDate);
}
