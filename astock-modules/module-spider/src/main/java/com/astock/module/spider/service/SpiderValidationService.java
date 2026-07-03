package com.astock.module.spider.service;

import java.time.LocalDate;
import java.util.Map;

public interface SpiderValidationService {

    Map<String, Object> validate(LocalDate tradeDate);
}
