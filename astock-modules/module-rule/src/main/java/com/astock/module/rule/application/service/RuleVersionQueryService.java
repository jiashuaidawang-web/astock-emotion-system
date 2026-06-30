package com.astock.module.rule.application.service;

import com.astock.module.rule.api.vo.RuleVersionManagePageVO;
import com.astock.module.rule.application.query.RuleVersionManagePageQuery;

public interface RuleVersionQueryService {
    RuleVersionManagePageVO queryManagePage(RuleVersionManagePageQuery query);
}
