package com.astock.module.risk.application.service;

import com.astock.module.risk.api.vo.RiskControlPageVO;
import com.astock.module.risk.application.query.RiskControlPageQuery;

public interface RiskControlQueryService {
    RiskControlPageVO queryRiskControlPage(RiskControlPageQuery query);
}
