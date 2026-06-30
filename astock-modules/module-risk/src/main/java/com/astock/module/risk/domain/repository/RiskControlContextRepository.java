package com.astock.module.risk.domain.repository;

import com.astock.module.risk.domain.model.RiskControlContext;
import java.time.LocalDate;

public interface RiskControlContextRepository {
    RiskControlContext load(LocalDate tradeDate, String marketScope);
}
