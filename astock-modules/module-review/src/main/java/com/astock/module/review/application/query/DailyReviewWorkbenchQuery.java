package com.astock.module.review.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * DailyReviewWorkbenchQuery 数据载体。
 */
@Data
public class DailyReviewWorkbenchQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
