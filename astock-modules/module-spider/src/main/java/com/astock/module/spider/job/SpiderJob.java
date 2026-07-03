package com.astock.module.spider.job;

import com.astock.module.spider.service.DcSpiderService;
import com.astock.module.spider.service.SpiderValidationService;
import com.astock.module.spider.ths.ThsSpiderService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpiderJob {

    private final DcSpiderService dcSpiderService;
    private final SpiderValidationService validationService;
    private final ThsSpiderService thsSpiderService;

    public Map<String, Object> runEastMoneyDaily(LocalDate tradeDate) {
        return dcSpiderService.syncAll(tradeDate);
    }

    public Map<String, Object> runThsDaily(LocalDate tradeDate) {
        return thsSpiderService.syncDaily(tradeDate);
    }

    public Map<String, Object> validate(LocalDate tradeDate) {
        return validationService.validate(tradeDate);
    }
}
