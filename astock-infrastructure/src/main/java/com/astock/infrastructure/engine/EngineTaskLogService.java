package com.astock.infrastructure.engine;

import com.astock.infrastructure.engine.entity.AlgorithmTaskLogEntity;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 引擎任务日志服务。
 *
 * <p>使用 MyBatis-Plus insert/updateById 写入 algorithm_task_log，保留原有任务开始与结束语义。</p>
 */
@Service
public class EngineTaskLogService {

    /** 算法任务日志 Mapper。 */
    private final AlgorithmTaskLogMapper mapper;

    /**
     * 创建引擎任务日志服务。
     *
     * @param mapper 算法任务日志 Mapper
     */
    public EngineTaskLogService(AlgorithmTaskLogMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 记录任务开始。
     *
     * @param command 引擎运行命令
     * @return 已写入数据库并回填主键的任务日志实体
     */
    public AlgorithmTaskLogEntity start(EngineRunCommand command) {
        AlgorithmTaskLogEntity record = new AlgorithmTaskLogEntity();
        record.setTaskName(command.getTaskName());
        record.setTaskType(command.getTaskType());
        record.setTradeDate(command.getTradeDate());
        record.setTaskStatus(EngineConstants.RUNNING);
        record.setStartedAt(LocalDateTime.now());
        record.setInputJson(command.getParamJson());
        record.setIsDeleted(0);
        mapper.insert(record);
        return record;
    }

    /**
     * 记录任务结束。
     *
     * @param record 任务日志实体
     * @param status 最终任务状态
     * @param outputJson 输出JSON或摘要文本
     * @param failureReason 失败原因
     */
    public void finish(AlgorithmTaskLogEntity record,
                       String status,
                       String outputJson,
                       String failureReason) {
        record.setTaskStatus(status);
        record.setFinishedAt(LocalDateTime.now());
        record.setCostMillis(Duration.between(record.getStartedAt(), record.getFinishedAt()).toMillis());
        record.setOutputJson(outputJson);
        record.setFailureReason(failureReason);
        mapper.updateById(record);
    }
}
