package com.astock.module.sector.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * SectorStrengthPageQuery 数据载体。
 */
@Data
public class SectorStrengthPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
