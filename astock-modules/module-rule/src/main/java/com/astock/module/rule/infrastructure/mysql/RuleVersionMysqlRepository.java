package com.astock.module.rule.infrastructure.mysql;

import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.astock.module.rule.domain.repository.RuleVersionSnapshotRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 * 规则版本页面快照仓储。
 *
 * <p>该仓储读取 MySQL rule_version 表并使用 MyBatis-Plus 条件查询。</p>
 */
@Repository
public class RuleVersionMysqlRepository implements RuleVersionSnapshotRepository {

    /** 规则版本 MyBatis-Plus Mapper。 */
    private final RuleVersionMysqlMapper mapper;

    /**
     * 创建规则版本页面快照仓储。
     *
     * @param mapper 规则版本 Mapper
     */
    public RuleVersionMysqlRepository(RuleVersionMysqlMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查询近期规则版本数据。
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
