package com.astock.module.sector.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.sector.api.vo.SectorStrengthPageVO;
import com.astock.module.sector.application.query.SectorStrengthPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SectorStrengthConverter implements PageBundleConverter<SectorStrengthPageQuery, SectorStrengthPageVO> {

    @Override
    public SectorStrengthPageVO convert(SectorStrengthPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        SectorStrengthPageVO vo = new SectorStrengthPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("sector_strength_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toSectorStrengthOverviewVO(bundle.firstRow("sector_strength_snapshot"), bundle));
        vo.setIndustryRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorStrengthRankVO(r, bundle)).toList());
        vo.setConceptRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorStrengthRankVO(r, bundle)).toList());
        vo.setManualThemeRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorStrengthRankVO(r, bundle)).toList());
        vo.setLimitUpDensityRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorLimitUpDensityRankVO(r, bundle)).toList());
        vo.setTurnoverRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorTurnoverRankVO(r, bundle)).toList());
        vo.setContinuityRanks(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorContinuityRankVO(r, bundle)).toList());
        vo.setSectorLadders(bundle.rows("sector_strength_snapshot").stream().map(r -> toSectorLadderVO(r, bundle)).toList());
        vo.setDivergenceRisks(bundle.rows("risk_signal_detail").stream().map(r -> toSectorDivergenceRiskVO(r, bundle)).toList());
        vo.setMainlineRelations(bundle.rows("mainline_daily_snapshot").stream().map(r -> toSectorMainlineRelationVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("sector_strength_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "SectorStrengthOverviewVO" -> "sector_strength_snapshot";
            case "SectorStrengthRankVO" -> "sector_strength_snapshot";
            case "SectorLimitUpDensityRankVO" -> "sector_strength_snapshot";
            case "SectorTurnoverRankVO" -> "sector_strength_snapshot";
            case "SectorContinuityRankVO" -> "sector_strength_snapshot";
            case "SectorLadderVO" -> "sector_strength_snapshot";
            case "SectorDivergenceRiskVO" -> "risk_signal_detail";
            case "SectorMainlineRelationVO" -> "mainline_daily_snapshot";
            case "SectorStrengthPageVO" -> "sector_strength_snapshot";
            default -> "sector_strength_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private SectorStrengthPageVO.SectorStrengthOverviewVO toSectorStrengthOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorStrengthOverviewVO item = new SectorStrengthPageVO.SectorStrengthOverviewVO();
        item.setSectorCount(MapFieldReader.integer(row, "sector_count"));
        item.setStrongestSectorName(MapFieldReader.string(row, "strongest_sector_name"));
        item.setStrongestScore(MapFieldReader.decimal(row, "strongest_score"));
        item.setMainlineRelatedCount(MapFieldReader.integer(row, "mainline_related_count"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private SectorStrengthPageVO.SectorStrengthRankVO toSectorStrengthRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorStrengthRankVO item = new SectorStrengthPageVO.SectorStrengthRankVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setSectorType(MapFieldReader.string(row, "sector_type"));
        item.setRankNo(MapFieldReader.integer(row, "rank_no"));
        item.setSectorStrengthScore(MapFieldReader.decimal(row, "sector_strength_score"));
        item.setPctChange(MapFieldReader.decimal(row, "pct_change"));
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setTurnoverAmount(MapFieldReader.decimal(row, "turnover_amount"));
        item.setMainlineRelated(MapFieldReader.bool(row, "mainline_related"));
        item.setRankText(MapFieldReader.string(row, "rank_text"));
        return item;
    }

    private SectorStrengthPageVO.SectorLimitUpDensityRankVO toSectorLimitUpDensityRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorLimitUpDensityRankVO item = new SectorStrengthPageVO.SectorLimitUpDensityRankVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setLimitUpDensityScore(MapFieldReader.decimal(row, "limit_up_density_score"));
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setStockCount(MapFieldReader.integer(row, "stock_count"));
        return item;
    }

    private SectorStrengthPageVO.SectorTurnoverRankVO toSectorTurnoverRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorTurnoverRankVO item = new SectorStrengthPageVO.SectorTurnoverRankVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setTurnoverAmount(MapFieldReader.decimal(row, "turnover_amount"));
        item.setTurnoverScore(MapFieldReader.decimal(row, "turnover_score"));
        return item;
    }

    private SectorStrengthPageVO.SectorContinuityRankVO toSectorContinuityRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorContinuityRankVO item = new SectorStrengthPageVO.SectorContinuityRankVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setContinuityScore(MapFieldReader.decimal(row, "continuity_score"));
        item.setContinuityText(MapFieldReader.string(row, "continuity_text"));
        return item;
    }

    private SectorStrengthPageVO.SectorLadderVO toSectorLadderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorLadderVO item = new SectorStrengthPageVO.SectorLadderVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setMaxBoardHeight(MapFieldReader.integer(row, "max_board_height"));
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setLadderText(MapFieldReader.string(row, "ladder_text"));
        return item;
    }

    private SectorStrengthPageVO.SectorDivergenceRiskVO toSectorDivergenceRiskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorDivergenceRiskVO item = new SectorStrengthPageVO.SectorDivergenceRiskVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setDivergenceRiskScore(MapFieldReader.decimal(row, "divergence_risk_score"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private SectorStrengthPageVO.SectorMainlineRelationVO toSectorMainlineRelationVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                SectorStrengthPageVO.SectorMainlineRelationVO item = new SectorStrengthPageVO.SectorMainlineRelationVO();
        item.setSectorCode(MapFieldReader.string(row, "sector_code"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setRelationText(MapFieldReader.string(row, "relation_text"));
        return item;
    }

}
