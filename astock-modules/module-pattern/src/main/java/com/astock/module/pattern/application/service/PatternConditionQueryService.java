package com.astock.module.pattern.application.service;

import com.astock.module.pattern.api.vo.PatternConditionPageVO;
import com.astock.module.pattern.application.query.PatternConditionPageQuery;

public interface PatternConditionQueryService {
    PatternConditionPageVO queryConditionPage(PatternConditionPageQuery query);
}
