package com.astock.infrastructure.rule;

import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 规则版本 Mapper。
 *
 * <p>规则版本存在性校验和当前启用版本查询统一使用 MyBatis-Plus 条件构造器。</p>
 */
@Mapper
public interface RuleVersionMapper extends BaseMapper<RuleVersionEntity> {
}
