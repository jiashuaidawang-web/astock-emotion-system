package com.astock.infrastructure.engine;

import com.astock.infrastructure.engine.entity.AlgorithmTaskLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 算法任务执行日志 Mapper。
 *
 * <p>任务日志新增与完成状态更新统一使用 MyBatis-Plus insert/updateById。</p>
 */
@Mapper
public interface AlgorithmTaskLogMapper extends BaseMapper<AlgorithmTaskLogEntity> {
}
