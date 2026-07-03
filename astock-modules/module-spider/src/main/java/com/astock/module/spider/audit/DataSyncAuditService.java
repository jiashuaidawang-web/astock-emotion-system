package com.astock.module.spider.audit;

import java.time.LocalDate;
import java.util.Map;

public interface DataSyncAuditService {

    void start(LocalDate tradeDate, String targetTable, String syncType);

    void markFetched(LocalDate tradeDate, String targetTable, String syncType, int sourceTotalCount, int fetchedCount);

    void markWriting(LocalDate tradeDate, String targetTable, String syncType);

    void finish(LocalDate tradeDate, String targetTable, String syncType, int insertedCount, Map<String, Object> features);

    void fail(LocalDate tradeDate, String targetTable, String syncType, Exception exception, Map<String, Object> features);
}
