package com.astock.module.spider.audit;

import com.astock.module.spider.domain.entity.DataSyncAuditLogEntity;
import com.astock.module.spider.enums.CheckStatus;
import com.astock.module.spider.enums.SyncStatus;
import com.astock.module.spider.mapper.DataSyncAuditLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataSyncAuditServiceImpl implements DataSyncAuditService {

    private static final String DAILY = "DAILY";

    private final DataSyncAuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void start(LocalDate tradeDate, String targetTable, String syncType) {
        DataSyncAuditLogEntity entity = findOrCreate(tradeDate, targetTable, syncType);
        entity.setRunDate(LocalDate.now());
        entity.setSyncStatus(SyncStatus.FETCHING.getCode());
        entity.setCheckStatus(CheckStatus.UNCHECKED.getCode());
        entity.setErrorMessage(null);
        if (entity.getId() == null) {
            auditLogMapper.insert(entity);
        } else {
            auditLogMapper.updateById(entity);
        }
    }

    @Override
    public void markFetched(LocalDate tradeDate, String targetTable, String syncType, int sourceTotalCount, int fetchedCount) {
        DataSyncAuditLogEntity entity = findOrCreate(tradeDate, targetTable, syncType);
        entity.setSourceTotalCount(sourceTotalCount);
        entity.setFetchedCount(fetchedCount);
        entity.setSyncStatus(SyncStatus.FETCHING.getCode());
        save(entity);
    }

    @Override
    public void markWriting(LocalDate tradeDate, String targetTable, String syncType) {
        DataSyncAuditLogEntity entity = findOrCreate(tradeDate, targetTable, syncType);
        entity.setSyncStatus(SyncStatus.WRITING_CLICKHOUSE.getCode());
        save(entity);
    }

    @Override
    public void finish(LocalDate tradeDate, String targetTable, String syncType, int insertedCount, Map<String, Object> features) {
        DataSyncAuditLogEntity entity = findOrCreate(tradeDate, targetTable, syncType);
        entity.setInsertedCount(insertedCount);
        entity.setSyncStatus(SyncStatus.SUCCESS.getCode());
        boolean matched = safeInt(entity.getSourceTotalCount()) == safeInt(entity.getFetchedCount())
                && safeInt(entity.getFetchedCount()) == insertedCount;
        entity.setCheckStatus(matched ? CheckStatus.SUCCESS.getCode() : CheckStatus.COUNT_MISMATCH.getCode());
        entity.setFeatures(toJson(features));
        save(entity);
    }

    @Override
    public void fail(LocalDate tradeDate, String targetTable, String syncType, Exception exception, Map<String, Object> features) {
        DataSyncAuditLogEntity entity = findOrCreate(tradeDate, targetTable, syncType);
        entity.setSyncStatus(SyncStatus.FAILED.getCode());
        entity.setCheckStatus(CheckStatus.SERIOUS_ERROR.getCode());
        entity.setErrorMessage(stackTrace(exception));
        entity.setFeatures(toJson(features));
        entity.setRetryCount(safeInt(entity.getRetryCount()) + 1);
        save(entity);
    }

    private DataSyncAuditLogEntity findOrCreate(LocalDate tradeDate, String targetTable, String syncType) {
        String realSyncType = syncType == null ? DAILY : syncType;
        DataSyncAuditLogEntity entity = auditLogMapper.selectOne(new LambdaQueryWrapper<DataSyncAuditLogEntity>()
                .eq(DataSyncAuditLogEntity::getTradeDate, tradeDate)
                .eq(DataSyncAuditLogEntity::getTargetTable, targetTable)
                .eq(DataSyncAuditLogEntity::getSyncType, realSyncType));
        if (entity != null) {
            return entity;
        }
        DataSyncAuditLogEntity created = new DataSyncAuditLogEntity();
        created.setTradeDate(tradeDate);
        created.setRunDate(LocalDate.now());
        created.setTargetTable(targetTable);
        created.setSyncType(realSyncType);
        created.setSourceTotalCount(0);
        created.setFetchedCount(0);
        created.setInsertedCount(0);
        created.setCheckStatus(CheckStatus.UNCHECKED.getCode());
        created.setSyncStatus(SyncStatus.CREATED.getCode());
        created.setRetryCount(0);
        created.setFeatures("{}");
        return created;
    }

    private void save(DataSyncAuditLogEntity entity) {
        if (entity.getId() == null) {
            auditLogMapper.insert(entity);
        } else {
            auditLogMapper.updateById(entity);
        }
    }

    private String toJson(Map<String, Object> features) {
        if (features == null || features.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(features);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String stackTrace(Exception exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
