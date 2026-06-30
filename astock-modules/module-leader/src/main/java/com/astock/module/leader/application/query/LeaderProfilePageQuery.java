package com.astock.module.leader.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * LeaderProfilePageQuery 数据载体。
 */
@Data
public class LeaderProfilePageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** stockCode 字段。 */
    private String stockCode;
}
