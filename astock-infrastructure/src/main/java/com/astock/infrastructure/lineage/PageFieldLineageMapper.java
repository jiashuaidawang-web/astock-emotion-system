package com.astock.infrastructure.lineage;

import com.astock.infrastructure.lineage.entity.PageContractFieldLineageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 页面字段血缘契约 Mapper。
 *
 * <p>字段血缘查询统一使用 MyBatis-Plus LambdaQueryWrapper 组合条件。</p>
 */
@Mapper
public interface PageFieldLineageMapper extends BaseMapper<PageContractFieldLineageEntity> {
}
