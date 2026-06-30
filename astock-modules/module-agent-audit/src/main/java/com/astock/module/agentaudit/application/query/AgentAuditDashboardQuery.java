package com.astock.module.agentaudit.application.query;

import java.time.LocalDate;
import lombok.Data;

/**
 * AgentAuditDashboardQuery 数据载体。
 */
@Data
public class AgentAuditDashboardQuery {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** auditDate 字段。 */
    private LocalDate auditDate;
    /** 市场范围。 */
    private String marketScope;
}
