package com.astock.module.sample.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistoricalCycleSampleFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_04_CYCLE_SAMPLE_LIBRARY";
    }

    @Override
    public String voClassName() {
        return "HistoricalCycleSamplePageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("HistoricalCycleSamplePageVO.query", "CLICKHOUSE", "historical_cycle_sample", "query", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("HistoricalCycleSamplePageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("HistoricalCycleSamplePageVO.overview", "CLICKHOUSE", "historical_cycle_sample", "overview", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.stageDistributions", "CLICKHOUSE", "historical_cycle_sample", "stage_distributions", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.typeDistributions", "CLICKHOUSE", "historical_cycle_sample", "type_distributions", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.confirmDistributions", "CLICKHOUSE", "historical_cycle_sample", "confirm_distributions", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.samplePage", "CLICKHOUSE", "historical_cycle_sample", "sample_page", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.highQualitySamples", "CLICKHOUSE", "historical_cycle_sample", "high_quality_samples", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.pendingConfirmSamples", "CLICKHOUSE", "historical_cycle_sample", "pending_confirm_samples", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.conclusion", "CLICKHOUSE", "historical_cycle_sample", "evidence_json", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.riskTips", "CLICKHOUSE", "historical_cycle_sample", "risk_json", "", false),
                new FieldMapping("HistoricalCycleSamplePageVO.tradeDate", "CLICKHOUSE", "historical_cycle_sample", "trade_date", "", false),
                new FieldMapping("CycleSampleOverviewVO.totalSampleCount", "CLICKHOUSE", "historical_cycle_sample", "total_sample_count", "", false),
                new FieldMapping("CycleSampleOverviewVO.confirmedSampleCount", "CLICKHOUSE", "historical_cycle_sample", "confirmed_sample_count", "", false),
                new FieldMapping("CycleSampleOverviewVO.pendingConfirmCount", "CLICKHOUSE", "historical_cycle_sample", "pending_confirm_count", "", false),
                new FieldMapping("CycleSampleOverviewVO.highQualitySampleCount", "CLICKHOUSE", "historical_cycle_sample", "high_quality_sample_count", "", false),
                new FieldMapping("CycleSampleOverviewVO.overviewText", "CLICKHOUSE", "historical_cycle_sample", "features", "", false),
                new FieldMapping("CycleSampleStageDistributionVO.stageCode", "CLICKHOUSE", "historical_cycle_sample", "primary_stage", "", false),
                new FieldMapping("CycleSampleStageDistributionVO.stageName", "CLICKHOUSE", "historical_cycle_sample", "primary_stage_name", "", false),
                new FieldMapping("CycleSampleStageDistributionVO.sampleCount", "CLICKHOUSE", "historical_cycle_sample", "sample_count", "", false),
                new FieldMapping("CycleSampleStageDistributionVO.ratio", "CLICKHOUSE", "historical_cycle_sample", "ratio", "", false),
                new FieldMapping("CycleSampleTypeDistributionVO.sampleType", "CLICKHOUSE", "historical_cycle_sample", "sample_type", "", false),
                new FieldMapping("CycleSampleTypeDistributionVO.sampleCount", "CLICKHOUSE", "historical_cycle_sample", "sample_count", "", false),
                new FieldMapping("CycleSampleTypeDistributionVO.ratio", "CLICKHOUSE", "historical_cycle_sample", "ratio", "", false),
                new FieldMapping("CycleSampleConfirmDistributionVO.confirmStatus", "CLICKHOUSE", "historical_cycle_sample", "confirm_status", "", false),
                new FieldMapping("CycleSampleConfirmDistributionVO.sampleCount", "CLICKHOUSE", "historical_cycle_sample", "sample_count", "", false),
                new FieldMapping("CycleSampleConfirmDistributionVO.ratio", "CLICKHOUSE", "historical_cycle_sample", "ratio", "", false),
                new FieldMapping("CycleSampleListItemVO.sampleId", "CLICKHOUSE", "historical_cycle_sample", "sample_id", "", false),
                new FieldMapping("CycleSampleListItemVO.tradeDate", "CLICKHOUSE", "historical_cycle_sample", "trade_date", "", false),
                new FieldMapping("CycleSampleListItemVO.sampleType", "CLICKHOUSE", "historical_cycle_sample", "sample_type", "", false),
                new FieldMapping("CycleSampleListItemVO.stageType", "CLICKHOUSE", "historical_cycle_sample", "stage_type", "", false),
                new FieldMapping("CycleSampleListItemVO.sampleStatus", "CLICKHOUSE", "historical_cycle_sample", "sample_status", "", false),
                new FieldMapping("CycleSampleListItemVO.sampleConfidence", "CLICKHOUSE", "historical_cycle_sample", "sample_confidence", "", false),
                new FieldMapping("CycleSampleListItemVO.strongestMainline", "CLICKHOUSE", "historical_cycle_sample", "strongest_mainline", "", false),
                new FieldMapping("CycleSampleListItemVO.leaderStockName", "CLICKHOUSE", "historical_cycle_sample", "leader_stock_name", "", false),
                new FieldMapping("CycleSampleListItemVO.future3dReturn", "CLICKHOUSE", "historical_cycle_sample", "future_3d_return", "", false),
                new FieldMapping("CycleSampleListItemVO.maxDrawdown", "CLICKHOUSE", "historical_cycle_sample", "max_drawdown", "", false),
                new FieldMapping("CycleSampleListItemVO.manuallyConfirmed", "CLICKHOUSE", "historical_cycle_sample", "manually_confirmed", "", false),
                new FieldMapping("CycleSampleListItemVO.sampleText", "CLICKHOUSE", "historical_cycle_sample", "evidence_json", "", false)
        );
    }
}
