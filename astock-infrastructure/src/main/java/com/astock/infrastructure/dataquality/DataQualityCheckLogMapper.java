package com.astock.infrastructure.dataquality;

import com.astock.infrastructure.dataquality.entity.DataQualityCheckLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据完整性检查日志 Mapper。
 *
 * <p>查询、新增、修改均走 MyBatis-Plus BaseMapper，避免手写 SQL 与表结构漂移。</p>
 */
@Mapper
public interface DataQualityCheckLogMapper extends BaseMapper<DataQualityCheckLogEntity> {
}
