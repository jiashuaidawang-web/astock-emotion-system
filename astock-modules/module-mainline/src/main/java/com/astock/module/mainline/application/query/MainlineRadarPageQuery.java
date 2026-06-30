package com.astock.module.mainline.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * MainlineRadarPageQuery 数据载体。
 */
@Data
public class MainlineRadarPageQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
