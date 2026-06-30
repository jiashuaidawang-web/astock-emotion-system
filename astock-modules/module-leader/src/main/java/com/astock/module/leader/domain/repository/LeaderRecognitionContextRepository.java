package com.astock.module.leader.domain.repository;

import com.astock.module.leader.domain.model.LeaderRecognitionContext;
import java.time.LocalDate;

public interface LeaderRecognitionContextRepository {
    LeaderRecognitionContext load(LocalDate tradeDate, String marketScope);
}
