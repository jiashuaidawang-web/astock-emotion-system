package com.astock.module.sample.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.sample.api.vo.HistoricalCycleSamplePageVO;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HistoricalCycleSampleConverter implements PageBundleConverter<HistoricalCycleSamplePageQuery, HistoricalCycleSamplePageVO> {

    @Override
    public HistoricalCycleSamplePageVO convert(HistoricalCycleSamplePageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        HistoricalCycleSamplePageVO vo = new HistoricalCycleSamplePageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("historical_cycle_sample");
                vo.setQuery(query);
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toCycleSampleOverviewVO(bundle.firstRow("historical_cycle_sample"), bundle));
        vo.setStageDistributions(bundle.rows("emotion_stage_snapshot").stream().map(r -> toCycleSampleStageDistributionVO(r, bundle)).toList());
        vo.setTypeDistributions(bundle.rows("historical_cycle_sample").stream().map(r -> toCycleSampleTypeDistributionVO(r, bundle)).toList());
        vo.setConfirmDistributions(bundle.rows("historical_cycle_sample").stream().map(r -> toCycleSampleConfirmDistributionVO(r, bundle)).toList());
        vo.setSamplePage(bundle.rows("historical_cycle_sample").stream().map(r -> toCycleSampleListItemVO(r, bundle)).toList());
        vo.setHighQualitySamples(bundle.rows("historical_cycle_sample").stream().map(r -> toCycleSampleListItemVO(r, bundle)).toList());
        vo.setPendingConfirmSamples(bundle.rows("historical_cycle_sample").stream().map(r -> toCycleSampleListItemVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("historical_cycle_sample"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_snapshot"), "risk_json"));
        vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "CycleSampleOverviewVO" -> "historical_cycle_sample";
            case "CycleSampleStageDistributionVO" -> "emotion_stage_snapshot";
            case "CycleSampleTypeDistributionVO" -> "historical_cycle_sample";
            case "CycleSampleConfirmDistributionVO" -> "historical_cycle_sample";
            case "CycleSampleListItemVO" -> "historical_cycle_sample";
            case "HistoricalCycleSamplePageVO" -> "historical_cycle_sample";
            default -> "historical_cycle_sample";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private HistoricalCycleSamplePageVO.CycleSampleOverviewVO toCycleSampleOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalCycleSamplePageVO.CycleSampleOverviewVO item = new HistoricalCycleSamplePageVO.CycleSampleOverviewVO();
        item.setTotalSampleCount(MapFieldReader.integer(row, "total_sample_count"));
        item.setConfirmedSampleCount(MapFieldReader.integer(row, "confirmed_sample_count"));
        item.setPendingConfirmCount(MapFieldReader.integer(row, "pending_confirm_count"));
        item.setHighQualitySampleCount(MapFieldReader.integer(row, "high_quality_sample_count"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private HistoricalCycleSamplePageVO.CycleSampleStageDistributionVO toCycleSampleStageDistributionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalCycleSamplePageVO.CycleSampleStageDistributionVO item = new HistoricalCycleSamplePageVO.CycleSampleStageDistributionVO();
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setStageName(MapFieldReader.string(row, "primary_stage_name"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setRatio(MapFieldReader.decimal(row, "ratio"));
        return item;
    }

    private HistoricalCycleSamplePageVO.CycleSampleTypeDistributionVO toCycleSampleTypeDistributionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalCycleSamplePageVO.CycleSampleTypeDistributionVO item = new HistoricalCycleSamplePageVO.CycleSampleTypeDistributionVO();
        item.setSampleType(MapFieldReader.string(row, "sample_type"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setRatio(MapFieldReader.decimal(row, "ratio"));
        return item;
    }

    private HistoricalCycleSamplePageVO.CycleSampleConfirmDistributionVO toCycleSampleConfirmDistributionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalCycleSamplePageVO.CycleSampleConfirmDistributionVO item = new HistoricalCycleSamplePageVO.CycleSampleConfirmDistributionVO();
        item.setConfirmStatus(MapFieldReader.string(row, "confirm_status"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setRatio(MapFieldReader.decimal(row, "ratio"));
        return item;
    }

    private HistoricalCycleSamplePageVO.CycleSampleListItemVO toCycleSampleListItemVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalCycleSamplePageVO.CycleSampleListItemVO item = new HistoricalCycleSamplePageVO.CycleSampleListItemVO();
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setSampleType(MapFieldReader.string(row, "sample_type"));
        item.setStageType(MapFieldReader.string(row, "stage_type"));
        item.setSampleStatus(MapFieldReader.string(row, "sample_status"));
        item.setSampleConfidence(MapFieldReader.decimal(row, "sample_confidence"));
        item.setStrongestMainline(MapFieldReader.string(row, "strongest_mainline"));
        item.setLeaderStockName(MapFieldReader.string(row, "leader_stock_name"));
        item.setFuture3dReturn(MapFieldReader.decimal(row, "future_3d_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setManuallyConfirmed(MapFieldReader.bool(row, "manually_confirmed"));
        item.setSampleText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

}
