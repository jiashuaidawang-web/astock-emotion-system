package com.astock.module.agentaudit.infrastructure.mysql;

import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.astock.module.agentaudit.domain.repository.AgentAuditSnapshotRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 * Agent 审计模块 MySQL 规则版本快照仓储。
 *
 * <p>该仓储读取 MySQL rule_version 表，查询语法已切换为 MyBatis-Plus selectMaps。</p>
 */
@Repository
public class AgentAuditMysqlRepository implements AgentAuditSnapshotRepository {

    /** 规则版本 MyBatis-Plus Mapper。 */
    private final AgentAuditMysqlMapper mapper;

    /**
     * 创建 Agent 审计规则版本快照仓储。
     *
     * @param mapper 规则版本 Mapper
     */
    public AgentAuditMysqlRepository(AgentAuditMysqlMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查询近期规则版本数据，用于 Agent 审计页面快照。
     *
     * @param tradeDate 交易日，占位保留，当前规则版本表不按交易日过滤
     * @param marketScope 市场范围，占位保留，当前规则版本表不按市场过滤
     * @param limit 返回行数上限
     * @return 规则版本 Map 列表
     */
    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        int safeLimit = limit <= 0 ? 20 : limit;
        return mapper.selectMaps(new QueryWrapper<RuleVersionEntity>()
                .eq("is_deleted", 0)
                .orderByDesc("updated_at")
                .last("limit " + safeLimit));
    }
}
