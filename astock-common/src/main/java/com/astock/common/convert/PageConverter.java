package com.astock.common.convert;

import com.astock.common.data.PageDataQualityVO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PageConverter<Q, V> {
    V convert(Q query, PageDataQualityVO quality, List<Map<String, Object>> rows);

    default LocalDate resolveTradeDate(LocalDate tradeDate) {
        return tradeDate == null ? LocalDate.now() : tradeDate;
    }
}
