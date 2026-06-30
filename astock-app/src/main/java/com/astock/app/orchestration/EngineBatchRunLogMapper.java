package com.astock.app.orchestration;

import com.astock.app.orchestration.entity.EngineBatchRunLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Engine 一键跑批批次日志 Mapper。
 *
 * <p>只继承 MyBatis-Plus BaseMapper，批次新增与更新统一通过 insert/updateById 完成。</p>
 */
@Mapper
public interface EngineBatchRunLogMapper extends BaseMapper<EngineBatchRunLogEntity> {
}
