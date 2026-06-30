package com.astock.module.sector.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectorStrengthFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_06_SECTOR_STRENGTH";
    }

    @Override
    public String voClassName() {
        return "SectorStrengthPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("SectorStrengthPageVO.tradeDate", "CLICKHOUSE", "sector_strength_snapshot", "trade_date", "", false),
                new FieldMapping("SectorStrengthPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("SectorStrengthPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("SectorStrengthPageVO.overview", "CLICKHOUSE", "sector_strength_snapshot", "overview", "", false),
                new FieldMapping("SectorStrengthPageVO.industryRanks", "CLICKHOUSE", "sector_strength_snapshot", "industry_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.conceptRanks", "CLICKHOUSE", "sector_strength_snapshot", "concept_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.manualThemeRanks", "CLICKHOUSE", "sector_strength_snapshot", "manual_theme_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.limitUpDensityRanks", "CLICKHOUSE", "sector_strength_snapshot", "limit_up_density_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.turnoverRanks", "CLICKHOUSE", "sector_strength_snapshot", "turnover_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.continuityRanks", "CLICKHOUSE", "sector_strength_snapshot", "continuity_ranks", "", false),
                new FieldMapping("SectorStrengthPageVO.sectorLadders", "CLICKHOUSE", "sector_strength_snapshot", "sector_ladders", "", false),
                new FieldMapping("SectorStrengthPageVO.divergenceRisks", "CLICKHOUSE", "sector_strength_snapshot", "divergence_risks", "", false),
                new FieldMapping("SectorStrengthPageVO.mainlineRelations", "CLICKHOUSE", "sector_strength_snapshot", "mainline_relations", "", false),
                new FieldMapping("SectorStrengthPageVO.conclusion", "CLICKHOUSE", "sector_strength_snapshot", "evidence_json", "", false),
                new FieldMapping("SectorStrengthPageVO.riskTips", "CLICKHOUSE", "sector_strength_snapshot", "risk_json", "", false),
                new FieldMapping("SectorStrengthOverviewVO.sectorCount", "CLICKHOUSE", "sector_strength_snapshot", "sector_count", "", false),
                new FieldMapping("SectorStrengthOverviewVO.strongestSectorName", "CLICKHOUSE", "sector_strength_snapshot", "strongest_sector_name", "", false),
                new FieldMapping("SectorStrengthOverviewVO.strongestScore", "CLICKHOUSE", "sector_strength_snapshot", "strongest_score", "", false),
                new FieldMapping("SectorStrengthOverviewVO.mainlineRelatedCount", "CLICKHOUSE", "sector_strength_snapshot", "mainline_related_count", "", false),
                new FieldMapping("SectorStrengthOverviewVO.overviewText", "CLICKHOUSE", "sector_strength_snapshot", "features", "", false),
                new FieldMapping("SectorStrengthRankVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorStrengthRankVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorStrengthRankVO.sectorType", "CLICKHOUSE", "sector_strength_snapshot", "sector_type", "", false),
                new FieldMapping("SectorStrengthRankVO.rankNo", "CLICKHOUSE", "sector_strength_snapshot", "rank_no", "", false),
                new FieldMapping("SectorStrengthRankVO.sectorStrengthScore", "CLICKHOUSE", "sector_strength_snapshot", "sector_strength_score", "", false),
                new FieldMapping("SectorStrengthRankVO.pctChange", "CLICKHOUSE", "sector_strength_snapshot", "pct_change", "", false),
                new FieldMapping("SectorStrengthRankVO.limitUpCount", "CLICKHOUSE", "sector_strength_snapshot", "limit_up_count", "", false),
                new FieldMapping("SectorStrengthRankVO.turnoverAmount", "CLICKHOUSE", "sector_strength_snapshot", "turnover_amount", "", false),
                new FieldMapping("SectorStrengthRankVO.mainlineRelated", "CLICKHOUSE", "sector_strength_snapshot", "mainline_related", "", false),
                new FieldMapping("SectorStrengthRankVO.rankText", "CLICKHOUSE", "sector_strength_snapshot", "rank_text", "", false),
                new FieldMapping("SectorLimitUpDensityRankVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorLimitUpDensityRankVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorLimitUpDensityRankVO.limitUpDensityScore", "CLICKHOUSE", "sector_strength_snapshot", "limit_up_density_score", "", false),
                new FieldMapping("SectorLimitUpDensityRankVO.limitUpCount", "CLICKHOUSE", "sector_strength_snapshot", "limit_up_count", "", false),
                new FieldMapping("SectorLimitUpDensityRankVO.stockCount", "CLICKHOUSE", "sector_strength_snapshot", "stock_count", "", false),
                new FieldMapping("SectorTurnoverRankVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorTurnoverRankVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorTurnoverRankVO.turnoverAmount", "CLICKHOUSE", "sector_strength_snapshot", "turnover_amount", "", false),
                new FieldMapping("SectorTurnoverRankVO.turnoverScore", "CLICKHOUSE", "sector_strength_snapshot", "turnover_score", "", false),
                new FieldMapping("SectorContinuityRankVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorContinuityRankVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorContinuityRankVO.continuityScore", "CLICKHOUSE", "sector_strength_snapshot", "continuity_score", "", false),
                new FieldMapping("SectorContinuityRankVO.continuityText", "CLICKHOUSE", "sector_strength_snapshot", "continuity_text", "", false),
                new FieldMapping("SectorLadderVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorLadderVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorLadderVO.maxBoardHeight", "CLICKHOUSE", "sector_strength_snapshot", "max_board_height", "", false),
                new FieldMapping("SectorLadderVO.limitUpCount", "CLICKHOUSE", "sector_strength_snapshot", "limit_up_count", "", false),
                new FieldMapping("SectorLadderVO.ladderText", "CLICKHOUSE", "sector_strength_snapshot", "ladder_text", "", false),
                new FieldMapping("SectorDivergenceRiskVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorDivergenceRiskVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorDivergenceRiskVO.divergenceRiskScore", "CLICKHOUSE", "sector_strength_snapshot", "divergence_risk_score", "", false),
                new FieldMapping("SectorDivergenceRiskVO.riskLevel", "CLICKHOUSE", "sector_strength_snapshot", "risk_level", "", false),
                new FieldMapping("SectorDivergenceRiskVO.riskText", "CLICKHOUSE", "sector_strength_snapshot", "risk_json", "", false),
                new FieldMapping("SectorMainlineRelationVO.sectorCode", "CLICKHOUSE", "sector_strength_snapshot", "sector_code", "", false),
                new FieldMapping("SectorMainlineRelationVO.sectorName", "CLICKHOUSE", "sector_strength_snapshot", "sector_name", "", false),
                new FieldMapping("SectorMainlineRelationVO.mainlineId", "CLICKHOUSE", "sector_strength_snapshot", "mainline_id", "", false),
                new FieldMapping("SectorMainlineRelationVO.mainlineName", "CLICKHOUSE", "sector_strength_snapshot", "mainline_name", "", false),
                new FieldMapping("SectorMainlineRelationVO.relationText", "CLICKHOUSE", "sector_strength_snapshot", "relation_text", "", false)
        );
    }
}
