package com.astock.module.pattern.domain.repository;

import com.astock.module.pattern.domain.model.PatternEngineSupportContext;
import java.time.LocalDate;

public interface PatternEngineSupportContextRepository {
    PatternEngineSupportContext load(LocalDate tradeDate, String marketScope);
}
