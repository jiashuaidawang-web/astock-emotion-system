package com.astock.module.mainline.domain.repository;

import com.astock.module.mainline.domain.model.MainlineRecognitionContext;

import java.time.LocalDate;

public interface MainlineRecognitionContextRepository {
    MainlineRecognitionContext load(LocalDate tradeDate, String marketScope);
}
