package com.astock.module.emotion.domain.repository;

import com.astock.module.emotion.domain.model.EmotionHistoricalContext;

import java.time.LocalDate;

public interface EmotionHistoricalContextRepository {
    EmotionHistoricalContext load(LocalDate tradeDate, String marketScope, int pathWindowDays);
}
