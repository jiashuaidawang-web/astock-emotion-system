package com.astock.app.orchestration;

import com.astock.app.orchestration.entity.EngineBatchStepLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Engine 一键跑批步骤日志 Mapper。
 *
 * <p>只继承 MyBatis-Plus BaseMapper，步骤日志写入统一通过 insert 完成。</p>
 */
@Mapper
public interface EngineBatchStepLogMapper extends BaseMapper<EngineBatchStepLogEntity> {
}
