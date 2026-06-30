package com.astock.module.rule.infrastructure.mysql;

import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 规则版本页面模块 MyBatis-Plus Mapper。
 *
 * <p>该 Mapper 复用 rule_version 表实体，列表查询由调用方使用 QueryWrapper/selectMaps 完成。</p>
 */
@Mapper
public interface RuleVersionMysqlMapper extends BaseMapper<RuleVersionEntity> {
}
