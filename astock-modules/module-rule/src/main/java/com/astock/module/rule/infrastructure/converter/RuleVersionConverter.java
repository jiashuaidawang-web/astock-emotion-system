package com.astock.module.rule.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.rule.api.vo.RuleVersionManagePageVO;
import com.astock.module.rule.application.query.RuleVersionManagePageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RuleVersionConverter implements PageBundleConverter<RuleVersionManagePageQuery, RuleVersionManagePageVO> {

    @Override
    public RuleVersionManagePageVO convert(RuleVersionManagePageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        RuleVersionManagePageVO vo = new RuleVersionManagePageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("backtest_layer_stat");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toRuleVersionOverviewVO(bundle.firstRow("backtest_layer_stat"), bundle));
        vo.setRuleDefinitions(bundle.rows("rule_version").stream().map(r -> toRuleDefinitionVO(r, bundle)).toList());
        vo.setVersions(bundle.rows("rule_version").stream().map(r -> toRuleVersionVO(r, bundle)).toList());
        vo.setActiveVersions(bundle.rows("rule_version").stream().map(r -> toRuleVersionVO(r, bundle)).toList());
        vo.setPublishChecks(bundle.rows("rule_version").stream().map(r -> toRulePublishCheckVO(r, bundle)).toList());
        vo.setBacktestChecks(bundle.rows("backtest_layer_stat").stream().map(r -> toRuleBacktestCheckVO(r, bundle)).toList());
        vo.setAuditLogs(bundle.rows("rule_version").stream().map(r -> toRuleVersionAuditLogVO(r, bundle)).toList());
        vo.setCompareResults(bundle.rows("rule_version").stream().map(r -> toRuleVersionCompareVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("backtest_layer_stat"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("backtest_layer_stat"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "RuleVersionOverviewVO" -> "rule_version";
            case "RuleDefinitionVO" -> "rule_version";
            case "RuleVersionVO" -> "rule_version";
            case "RulePublishCheckVO" -> "rule_version";
            case "RuleBacktestCheckVO" -> "backtest_layer_stat";
            case "RuleVersionAuditLogVO" -> "rule_version";
            case "RuleVersionCompareVO" -> "rule_version";
            case "RuleVersionManagePageVO" -> "rule_version";
            default -> "backtest_layer_stat";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private RuleVersionManagePageVO.RuleVersionOverviewVO toRuleVersionOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleVersionOverviewVO item = new RuleVersionManagePageVO.RuleVersionOverviewVO();
        item.setRuleCount(MapFieldReader.integer(row, "rule_count"));
        item.setVersionCount(MapFieldReader.integer(row, "version_count"));
        item.setDraftCount(MapFieldReader.integer(row, "draft_count"));
        item.setPublishedCount(MapFieldReader.integer(row, "published_count"));
        item.setActiveCount(MapFieldReader.integer(row, "active_count"));
        item.setPublishBlockedCount(MapFieldReader.integer(row, "publish_blocked_count"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private RuleVersionManagePageVO.RuleDefinitionVO toRuleDefinitionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleDefinitionVO item = new RuleVersionManagePageVO.RuleDefinitionVO();
        item.setRuleId(MapFieldReader.longValue(row, "rule_id"));
        item.setRuleCode(MapFieldReader.string(row, "rule_code"));
        item.setRuleName(MapFieldReader.string(row, "rule_name"));
        item.setRuleType(MapFieldReader.string(row, "rule_type"));
        item.setEnabled(MapFieldReader.bool(row, "enabled"));
        item.setBacktestRequired(MapFieldReader.bool(row, "backtest_required"));
        item.setAgentAuditRequired(MapFieldReader.bool(row, "agent_audit_required"));
        return item;
    }

    private RuleVersionManagePageVO.RuleVersionVO toRuleVersionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleVersionVO item = new RuleVersionManagePageVO.RuleVersionVO();
        item.setVersionId(MapFieldReader.longValue(row, "id"));
        item.setRuleCode(MapFieldReader.string(row, "rule_code"));
        item.setVersionNo(MapFieldReader.string(row, "version_no"));
        item.setVersionName(MapFieldReader.string(row, "version_name"));
        item.setVersionStatus(MapFieldReader.string(row, "version_status"));
        item.setActive(MapFieldReader.bool(row, "active"));
        item.setBacktestCheckPassed(MapFieldReader.bool(row, "backtest_check_passed"));
        item.setPublishCheckPassed(MapFieldReader.bool(row, "publish_check_passed"));
        item.setAgentAuditPassed(MapFieldReader.bool(row, "agent_audit_passed"));
        item.setVersionDescription(MapFieldReader.string(row, "version_description"));
        return item;
    }

    private RuleVersionManagePageVO.RulePublishCheckVO toRulePublishCheckVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RulePublishCheckVO item = new RuleVersionManagePageVO.RulePublishCheckVO();
        item.setVersionId(MapFieldReader.longValue(row, "id"));
        item.setCheckCode(MapFieldReader.string(row, "check_code"));
        item.setCheckName(MapFieldReader.string(row, "check_name"));
        item.setPassed(MapFieldReader.bool(row, "passed"));
        item.setBlockPublish(MapFieldReader.bool(row, "block_publish"));
        item.setFailedReason(MapFieldReader.string(row, "failed_reason"));
        item.setFixSuggestion(MapFieldReader.string(row, "fix_suggestion"));
        return item;
    }

    private RuleVersionManagePageVO.RuleBacktestCheckVO toRuleBacktestCheckVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleBacktestCheckVO item = new RuleVersionManagePageVO.RuleBacktestCheckVO();
        item.setVersionId(MapFieldReader.longValue(row, "id"));
        item.setBacktestPassed(MapFieldReader.bool(row, "backtest_passed"));
        item.setLatestReportId(MapFieldReader.longValue(row, "latest_report_id"));
        item.setCheckText(MapFieldReader.string(row, "check_text"));
        return item;
    }

    private RuleVersionManagePageVO.RuleVersionAuditLogVO toRuleVersionAuditLogVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleVersionAuditLogVO item = new RuleVersionManagePageVO.RuleVersionAuditLogVO();
        item.setVersionId(MapFieldReader.longValue(row, "id"));
        item.setOperationType(MapFieldReader.string(row, "operation_type"));
        item.setOperator(MapFieldReader.string(row, "operator"));
        item.setOperatedAt(MapFieldReader.localDateTime(row, "operated_at"));
        item.setOperationRemark(MapFieldReader.string(row, "operation_remark"));
        return item;
    }

    private RuleVersionManagePageVO.RuleVersionCompareVO toRuleVersionCompareVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RuleVersionManagePageVO.RuleVersionCompareVO item = new RuleVersionManagePageVO.RuleVersionCompareVO();
        item.setBaseVersionId(MapFieldReader.longValue(row, "base_version_id"));
        item.setCompareVersionId(MapFieldReader.longValue(row, "compare_version_id"));
        item.setDiffText(MapFieldReader.string(row, "diff_text"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

}
