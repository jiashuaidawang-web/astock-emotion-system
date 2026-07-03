package com.astock.module.spider.service.impl;

import com.astock.module.spider.service.SpiderClickHouseRepository;
import com.astock.module.spider.service.SpiderValidationService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpiderValidationServiceImpl implements SpiderValidationService {

    private final SpiderClickHouseRepository clickHouseRepository;

    @Override
    public Map<String, Object> validate(LocalDate tradeDate) {
        return clickHouseRepository.validateDaily(tradeDate);
    }
}
