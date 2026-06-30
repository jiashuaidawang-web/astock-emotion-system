package com.astock.module.agentaudit.domain.service;

import com.astock.module.agentaudit.domain.model.AgentAuditFileSnapshot;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AgentAuditProjectScanner {
    private static final List<String> AUDIT_SUFFIXES = List.of(".java", ".xml", ".yml", ".yaml", ".sql", ".vue", ".ts", ".js");

    public List<AgentAuditFileSnapshot> scan(String projectRootPath) {
        Path root = Path.of(projectRootPath == null || projectRootPath.isBlank() ? "." : projectRootPath).toAbsolutePath().normalize();
        try {
            return Files.walk(root)
                    .filter(Files::isRegularFile)
                    .filter(path -> AUDIT_SUFFIXES.stream().anyMatch(suffix -> path.getFileName().toString().endsWith(suffix)))
                    .filter(path -> !path.toString().contains("/target/"))
                    .filter(path -> !path.toString().contains("/node_modules/"))
                    .filter(path -> !path.toString().contains("/.git/"))
                    .map(path -> toSnapshot(root, path))
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("项目文件扫描失败：" + ex.getMessage(), ex);
        }
    }

    private AgentAuditFileSnapshot toSnapshot(Path root, Path path) {
        AgentAuditFileSnapshot snapshot = new AgentAuditFileSnapshot();
        snapshot.setPath(path);
        snapshot.setRelativePath(root.relativize(path).toString());
        snapshot.setModuleName(resolveModuleName(snapshot.getRelativePath()));
        snapshot.setFileType(resolveFileType(path));
        try {
            snapshot.setContent(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            snapshot.setContent("");
        }
        return snapshot;
    }

    private String resolveModuleName(String relativePath) {
        String normalized = relativePath.replace("\\", "/");
        if (normalized.startsWith("astock-app")) return "astock-app";
        if (normalized.startsWith("astock-common")) return "astock-common";
        if (normalized.startsWith("astock-infrastructure")) return "astock-infrastructure";
        if (normalized.contains("module-")) {
            int idx = normalized.indexOf("module-");
            int end = normalized.indexOf("/", idx);
            return end < 0 ? normalized.substring(idx) : normalized.substring(idx, end);
        }
        return "ROOT";
    }

    private String resolveFileType(Path path) {
        String name = path.getFileName().toString();
        int idx = name.lastIndexOf('.');
        return idx < 0 ? "UNKNOWN" : name.substring(idx + 1).toUpperCase();
    }
}
