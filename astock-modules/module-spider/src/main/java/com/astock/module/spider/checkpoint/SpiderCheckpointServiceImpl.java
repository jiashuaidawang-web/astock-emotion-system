package com.astock.module.spider.checkpoint;

import com.astock.module.spider.domain.entity.SpiderTaskCheckpointEntity;
import com.astock.module.spider.mapper.SpiderTaskCheckpointMapper;
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
public class SpiderCheckpointServiceImpl implements SpiderCheckpointService {

    private final SpiderTaskCheckpointMapper checkpointMapper;
    private final ObjectMapper objectMapper;

    @Override
    public SpiderTaskCheckpointEntity getOrCreate(LocalDate tradeDate, int sourceType, String taskCode, String bizKey) {
        String realBizKey = normalizeBizKey(bizKey);
        SpiderTaskCheckpointEntity entity = checkpointMapper.selectOne(new LambdaQueryWrapper<SpiderTaskCheckpointEntity>()
                .eq(SpiderTaskCheckpointEntity::getTradeDate, tradeDate)
                .eq(SpiderTaskCheckpointEntity::getSourceType, sourceType)
                .eq(SpiderTaskCheckpointEntity::getTaskCode, taskCode)
                .eq(SpiderTaskCheckpointEntity::getBizKey, realBizKey));
        if (entity != null) {
            return entity;
        }
        SpiderTaskCheckpointEntity created = new SpiderTaskCheckpointEntity();
        created.setTradeDate(tradeDate);
        created.setSourceType(sourceType);
        created.setTaskCode(taskCode);
        created.setBizKey(realBizKey);
        created.setCurrentPage(0);
        created.setTotalPage(0);
        created.setSourceTotalCount(0);
        created.setFetchedCount(0);
        created.setInsertedCount(0);
        created.setStatus(SpiderCheckpointStatus.CREATED.getCode());
        created.setFeatures("{}");
        checkpointMapper.insert(created);
        return created;
    }

    @Override
    public boolean completed(LocalDate tradeDate, int sourceType, String taskCode, String bizKey) {
        return getOrCreate(tradeDate, sourceType, taskCode, bizKey).getStatus() == SpiderCheckpointStatus.COMPLETED.getCode();
    }

    @Override
    public int nextPage(LocalDate tradeDate, int sourceType, String taskCode, String bizKey) {
        SpiderTaskCheckpointEntity checkpoint = getOrCreate(tradeDate, sourceType, taskCode, bizKey);
        if (checkpoint.getStatus() == SpiderCheckpointStatus.COMPLETED.getCode()) {
            return -1;
        }
        return Math.max(1, safeInt(checkpoint.getCurrentPage()) + 1);
    }

    @Override
    public void markPageSuccess(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                                int currentPage, int totalPage, int sourceTotalCount, int fetchedCount,
                                int insertedCount, Map<String, Object> features) {
        SpiderTaskCheckpointEntity checkpoint = getOrCreate(tradeDate, sourceType, taskCode, bizKey);
        checkpoint.setCurrentPage(currentPage);
        checkpoint.setTotalPage(totalPage);
        checkpoint.setSourceTotalCount(sourceTotalCount);
        checkpoint.setFetchedCount(safeInt(checkpoint.getFetchedCount()) + fetchedCount);
        checkpoint.setInsertedCount(safeInt(checkpoint.getInsertedCount()) + insertedCount);
        checkpoint.setStatus(SpiderCheckpointStatus.RUNNING.getCode());
        checkpoint.setErrorMessage(null);
        checkpoint.setFeatures(toJson(features));
        checkpointMapper.updateById(checkpoint);
    }

    @Override
    public void markCompleted(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                              int sourceTotalCount, int fetchedCount, int insertedCount, Map<String, Object> features) {
        SpiderTaskCheckpointEntity checkpoint = getOrCreate(tradeDate, sourceType, taskCode, bizKey);
        checkpoint.setCurrentPage(Math.max(safeInt(checkpoint.getCurrentPage()), safeInt(checkpoint.getTotalPage())));
        checkpoint.setSourceTotalCount(sourceTotalCount);
        checkpoint.setFetchedCount(fetchedCount);
        checkpoint.setInsertedCount(insertedCount);
        checkpoint.setStatus(SpiderCheckpointStatus.COMPLETED.getCode());
        checkpoint.setErrorMessage(null);
        checkpoint.setFeatures(toJson(features));
        checkpointMapper.updateById(checkpoint);
    }

    @Override
    public void markFailed(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                           Exception exception, Map<String, Object> features) {
        SpiderTaskCheckpointEntity checkpoint = getOrCreate(tradeDate, sourceType, taskCode, bizKey);
        checkpoint.setStatus(SpiderCheckpointStatus.FAILED.getCode());
        checkpoint.setErrorMessage(stackTrace(exception));
        checkpoint.setFeatures(toJson(features));
        checkpointMapper.updateById(checkpoint);
    }

    private String normalizeBizKey(String bizKey) {
        return bizKey == null || bizKey.isBlank() ? "DEFAULT" : bizKey;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
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
}
