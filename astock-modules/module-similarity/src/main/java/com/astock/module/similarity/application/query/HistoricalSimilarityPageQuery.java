package com.astock.module.similarity.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * HistoricalSimilarityPageQuery 数据载体。
 */
@Data
public class HistoricalSimilarityPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
