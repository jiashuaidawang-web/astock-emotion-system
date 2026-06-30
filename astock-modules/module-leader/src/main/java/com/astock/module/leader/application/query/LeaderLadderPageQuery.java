package com.astock.module.leader.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * LeaderLadderPageQuery 数据载体。
 */
@Data
public class LeaderLadderPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
