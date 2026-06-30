package com.astock.module.agentaudit.infrastructure.mysql;

import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 审计模块读取规则版本的 MyBatis-Plus Mapper。
 *
 * <p>该 Mapper 复用 rule_version 表实体，近期规则版本列表由调用方使用 selectMaps 条件查询生成。</p>
 */
@Mapper
public interface AgentAuditMysqlMapper extends BaseMapper<RuleVersionEntity> {
}
