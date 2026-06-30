package com.astock.module.emotion.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * EmotionCycleStateMachineQuery 数据载体。
 */
@Data
public class EmotionCycleStateMachineQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
}
