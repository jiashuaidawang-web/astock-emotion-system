package com.astock.module.agentaudit.domain.model;

import java.nio.file.Path;
import lombok.Data;

/**
 * AgentAuditFileSnapshot 数据载体。
 */
@Data
public class AgentAuditFileSnapshot {
    /** path 字段。 */
    private Path path;
    /** relativePath 字段。 */
    private String relativePath;
    /** moduleName 字段。 */
    private String moduleName;
    /** fileType 字段。 */
    private String fileType;
    /** content 字段。 */
    private String content;
}
