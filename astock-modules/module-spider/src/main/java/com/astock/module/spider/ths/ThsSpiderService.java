package com.astock.module.spider.ths;

import java.time.LocalDate;
import java.util.Map;

public interface ThsSpiderService {

    Map<String, Object> syncDaily(LocalDate tradeDate);
}
