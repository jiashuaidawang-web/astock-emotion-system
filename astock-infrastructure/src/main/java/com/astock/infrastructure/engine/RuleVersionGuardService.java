package com.astock.infrastructure.engine;

import com.astock.common.exception.BusinessException;
import com.astock.infrastructure.rule.RuleVersionMapper;
import com.astock.infrastructure.rule.entity.RuleVersionEntity;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

/**
 * 规则版本守卫服务。
 *
 * <p>所有引擎执行前必须解析到明确的规则版本，防止算法在无版本约束下运行。</p>
 */
@Service
public class RuleVersionGuardService {

    /** 规则版本 Mapper。 */
    private final RuleVersionMapper ruleVersionMapper;

    /**
     * 创建规则版本守卫服务。
     *
     * @param ruleVersionMapper 规则版本 Mapper
     */
    public RuleVersionGuardService(RuleVersionMapper ruleVersionMapper) {
        this.ruleVersionMapper = ruleVersionMapper;
    }

    /**
     * 解析规则版本ID。
     *
     * @param ruleCode 规则编码
     * @param requestedRuleVersionId 调用方指定的规则版本ID
     * @return 已确认存在的规则版本ID
     */
    public Long resolveRuleVersionId(String ruleCode, Long requestedRuleVersionId) {
        if (requestedRuleVersionId != null) {
            Long count = ruleVersionMapper.selectCount(Wrappers.<RuleVersionEntity>lambdaQuery()
                    .eq(RuleVersionEntity::getId, requestedRuleVersionId)
                    .eq(RuleVersionEntity::getIsDeleted, 0));
            if (count == null || count <= 0) {
                throw new BusinessException("RULE_VERSION_MISSING", "规则版本不存在：" + requestedRuleVersionId);
            }
            return requestedRuleVersionId;
        }

        RuleVersionEntity active = ruleVersionMapper.selectOne(Wrappers.<RuleVersionEntity>lambdaQuery()
                .eq(RuleVersionEntity::getRuleCode, ruleCode)
                .eq(RuleVersionEntity::getActiveFlag, 1)
                .eq(RuleVersionEntity::getIsDeleted, 0)
                .last("limit 1"));
        if (active == null) {
            throw new BusinessException("RULE_VERSION_MISSING", "未找到启用规则版本：" + ruleCode);
        }
        return active.getId();
    }
}
