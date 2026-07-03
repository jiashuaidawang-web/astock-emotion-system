package com.astock.module.spider.checkpoint;

import com.astock.module.spider.domain.entity.SpiderTaskCheckpointEntity;
import java.time.LocalDate;
import java.util.Map;

public interface SpiderCheckpointService {

    SpiderTaskCheckpointEntity getOrCreate(LocalDate tradeDate, int sourceType, String taskCode, String bizKey);

    boolean completed(LocalDate tradeDate, int sourceType, String taskCode, String bizKey);

    int nextPage(LocalDate tradeDate, int sourceType, String taskCode, String bizKey);

    void markPageSuccess(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                         int currentPage, int totalPage, int sourceTotalCount, int fetchedCount,
                         int insertedCount, Map<String, Object> features);

    void markCompleted(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                       int sourceTotalCount, int fetchedCount, int insertedCount, Map<String, Object> features);

    void markFailed(LocalDate tradeDate, int sourceType, String taskCode, String bizKey,
                    Exception exception, Map<String, Object> features);
}
