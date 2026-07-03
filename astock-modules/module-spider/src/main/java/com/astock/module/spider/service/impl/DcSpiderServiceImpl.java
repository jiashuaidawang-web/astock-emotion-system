package com.astock.module.spider.service.impl;

import com.astock.module.spider.audit.DataSyncAuditService;
import com.astock.module.spider.checkpoint.SpiderCheckpointService;
import com.astock.module.spider.dc.DcApiClient;
import com.astock.module.spider.dc.DcEndpoint;
import com.astock.module.spider.dc.DcFieldMapper;
import com.astock.module.spider.dc.model.DcPoolResult;
import com.astock.module.spider.dc.model.DcSinglePageResult;
import com.astock.module.spider.dc.model.DcPageResult;
import com.astock.module.spider.domain.entity.SpiderTaskCheckpointEntity;
import com.astock.module.spider.domain.entity.StockDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDimensionRow;
import com.astock.module.spider.domain.entity.StockPlateRelationRow;
import com.astock.module.spider.domain.entity.StockPoolDailySnapshotRow;
import com.astock.module.spider.service.DcSpiderService;
import com.astock.module.spider.service.SpiderClickHouseRepository;
import com.astock.module.spider.enums.SpiderSourceType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DcSpiderServiceImpl implements DcSpiderService {

    private static final String DAILY = "DAILY";

    private final DcApiClient dcApiClient;
    private final DcFieldMapper fieldMapper;
    private final SpiderClickHouseRepository clickHouseRepository;
    private final DataSyncAuditService auditService;
    private final SpiderCheckpointService checkpointService;

    @Override
    public Map<String, Object> syncAll(LocalDate tradeDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("stockDailyInserted", syncStockDailyKline(tradeDate));
        result.put("plateInserted", syncPlates(tradeDate));
        result.put("relationInserted", syncPlateRelations(tradeDate));
        result.put("poolInserted", syncPools(tradeDate));
        result.put("validation", clickHouseRepository.validateDaily(tradeDate));
        return result;
    }

    @Override
    public int syncStockDailyKline(LocalDate tradeDate) {
        String table = "stock_daily_kline";
        auditService.start(tradeDate, table, DAILY);
        try {
            SpiderTaskCheckpointEntity checkpoint = checkpointService.getOrCreate(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT");
            int sourceTotal = safeInt(checkpoint.getSourceTotalCount());
            int fetched = safeInt(checkpoint.getFetchedCount());
            int inserted = safeInt(checkpoint.getInsertedCount());
            int nextPage = checkpointService.nextPage(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT");
            if (nextPage < 0) {
                return 0;
            }
            for (int page = nextPage; ; page++) {
                DcSinglePageResult singlePage = dcApiClient.fetchPage(DcEndpoint.ALL_STOCK, page);
                sourceTotal = singlePage.sourceTotalCount();
                List<StockDailyKlineRow> rows = singlePage.rows().stream()
                        .map(node -> fieldMapper.toStockDailyKline(tradeDate, node))
                        .filter(row -> row.getStockCode() != null && !row.getStockCode().isBlank())
                        .toList();
                auditService.markWriting(tradeDate, table, DAILY);
                int pageInserted = clickHouseRepository.insertStockDailyKline(rows);
                fetched += rows.size();
                inserted += pageInserted;
                checkpointService.markPageSuccess(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT",
                        page, singlePage.totalPage(), sourceTotal, rows.size(), pageInserted, Map.of("page", page));
                if (page >= singlePage.totalPage()) {
                    break;
                }
            }
            auditService.markFetched(tradeDate, table, DAILY, sourceTotal, fetched);
            auditService.markWriting(tradeDate, table, DAILY);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of("source", "east_money"));
            checkpointService.markCompleted(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT",
                    sourceTotal, fetched, inserted, Map.of("source", "east_money"));
            return inserted;
        } catch (Exception e) {
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "east_money"));
            checkpointService.markFailed(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT", e, Map.of("source", "east_money"));
            throw e;
        }
    }

    @Override
    public int syncPlates(LocalDate tradeDate) {
        List<DcEndpoint> endpoints = List.of(DcEndpoint.CONCEPT_PLATE, DcEndpoint.REGION_PLATE, DcEndpoint.INDUSTRY_PLATE);
        int inserted = 0;
        for (DcEndpoint endpoint : endpoints) {
            inserted += syncPlateEndpoint(tradeDate, endpoint);
        }
        return inserted;
    }

    @Override
    public int syncPlateRelations(LocalDate tradeDate) {
        String table = "stock_plate_relation";
        auditService.start(tradeDate, table, DAILY);
        try {
            List<StockPlateDimensionRow> plates = fetchAllEastMoneyPlates();
            List<StockPlateRelationRow> relations = new ArrayList<>();
            int sourceTotal = 0;
            int fetched = 0;
            int inserted = 0;
            for (StockPlateDimensionRow plate : plates) {
                String bizKey = plate.getPlateCode();
                int nextPage = checkpointService.nextPage(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, bizKey);
                if (nextPage < 0) {
                    continue;
                }
                int plateFetched = 0;
                int plateInserted = 0;
                int plateSourceTotal = 0;
                for (int page = nextPage; ; page++) {
                    DcSinglePageResult pageResult = dcApiClient.fetchPlateStocksPage(plate.getPlateCode(), page);
                    plateSourceTotal = pageResult.sourceTotalCount();
                    List<StockPlateRelationRow> pageRows = pageResult.rows().stream()
                            .map(row -> fieldMapper.toPlateRelation(tradeDate, plate, row))
                            .toList();
                    auditService.markWriting(tradeDate, table, DAILY);
                    int pageInserted = clickHouseRepository.insertPlateRelations(pageRows);
                    plateFetched += pageRows.size();
                    plateInserted += pageInserted;
                    checkpointService.markPageSuccess(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, bizKey,
                            page, pageResult.totalPage(), plateSourceTotal, pageRows.size(), pageInserted,
                            Map.of("plateCode", plate.getPlateCode(), "plateName", plate.getPlateName()));
                    if (page >= pageResult.totalPage()) {
                        break;
                    }
                }
                checkpointService.markCompleted(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, bizKey,
                        plateSourceTotal, plateFetched, plateInserted,
                        Map.of("plateCode", plate.getPlateCode(), "plateName", plate.getPlateName()));
                sourceTotal += plateSourceTotal;
                fetched += plateFetched;
                inserted += plateInserted;
            }
            auditService.markFetched(tradeDate, table, DAILY, sourceTotal, fetched);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of("source", "east_money", "plateCount", plates.size()));
            return inserted;
        } catch (Exception e) {
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "east_money"));
            throw e;
        }
    }

    @Override
    public int syncPools(LocalDate tradeDate) {
        int inserted = 0;
        for (DcEndpoint endpoint : EnumSet.of(
                DcEndpoint.LIMIT_UP_POOL,
                DcEndpoint.YEST_LIMIT_UP_POOL,
                DcEndpoint.STRONG_POOL,
                DcEndpoint.SUB_NEW_POOL,
                DcEndpoint.BROKEN_POOL,
                DcEndpoint.LIMIT_DOWN_POOL)) {
            inserted += syncPoolEndpoint(tradeDate, endpoint);
        }
        return inserted;
    }

    private int syncPlateEndpoint(LocalDate tradeDate, DcEndpoint endpoint) {
        String table = "stock_plate_daily_kline_" + endpoint.name().toLowerCase();
        auditService.start(tradeDate, table, DAILY);
        try {
            SpiderTaskCheckpointEntity checkpoint = checkpointService.getOrCreate(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT");
            int sourceTotal = safeInt(checkpoint.getSourceTotalCount());
            int fetched = safeInt(checkpoint.getFetchedCount());
            int inserted = safeInt(checkpoint.getInsertedCount());
            int dimensionInserted = 0;
            int nextPage = checkpointService.nextPage(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT");
            if (nextPage < 0) {
                return 0;
            }
            for (int page = nextPage; ; page++) {
                DcSinglePageResult singlePage = dcApiClient.fetchPage(endpoint, page);
                sourceTotal = singlePage.sourceTotalCount();
                List<StockPlateDimensionRow> dimensions = singlePage.rows().stream()
                        .map(node -> fieldMapper.toPlateDimension(endpoint.getPlateType(), node))
                        .toList();
                List<StockPlateDailyKlineRow> dailyRows = singlePage.rows().stream()
                        .map(node -> fieldMapper.toPlateDailyKline(tradeDate, endpoint.getPlateType(), node))
                        .toList();
                auditService.markWriting(tradeDate, table, DAILY);
                dimensionInserted += clickHouseRepository.insertPlateDimensions(dimensions);
                int pageInserted = clickHouseRepository.insertPlateDailyKline(dailyRows);
                fetched += dailyRows.size();
                inserted += pageInserted;
                checkpointService.markPageSuccess(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT",
                        page, singlePage.totalPage(), sourceTotal, dailyRows.size(), pageInserted,
                        Map.of("plateType", endpoint.getPlateType(), "page", page));
                if (page >= singlePage.totalPage()) {
                    break;
                }
            }
            auditService.markFetched(tradeDate, table, DAILY, sourceTotal, fetched);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of(
                    "source", "east_money",
                    "plateType", endpoint.getPlateType(),
                    "dimensionInserted", dimensionInserted));
            checkpointService.markCompleted(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT",
                    sourceTotal, fetched, inserted, Map.of("source", "east_money", "plateType", endpoint.getPlateType()));
            return inserted;
        } catch (Exception e) {
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "east_money", "plateType", endpoint.getPlateType()));
            checkpointService.markFailed(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT", e,
                    Map.of("source", "east_money", "plateType", endpoint.getPlateType()));
            throw e;
        }
    }

    private int syncPoolEndpoint(LocalDate tradeDate, DcEndpoint endpoint) {
        String table = "stock_pool_daily_snapshot_" + endpoint.getPoolType().getCode().toLowerCase();
        auditService.start(tradeDate, table, DAILY);
        try {
            if (checkpointService.completed(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT")) {
                return 0;
            }
            DcPoolResult poolResult = dcApiClient.fetchPool(endpoint, tradeDate);
            List<StockPoolDailySnapshotRow> rows = poolResult.rows().stream()
                    .map(node -> fieldMapper.toPoolSnapshot(tradeDate, endpoint.getPoolType(), node))
                    .toList();
            auditService.markFetched(tradeDate, table, DAILY, poolResult.sourceTotalCount(), rows.size());
            auditService.markWriting(tradeDate, table, DAILY);
            int inserted = clickHouseRepository.insertPoolSnapshots(rows);
            auditService.finish(tradeDate, table, DAILY, inserted, Map.of(
                    "source", "east_money",
                    "poolType", endpoint.getPoolType().getCode(),
                    "queryDate", poolResult.queryDate().toString()));
            checkpointService.markCompleted(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT",
                    poolResult.sourceTotalCount(), rows.size(), inserted,
                    Map.of("source", "east_money", "poolType", endpoint.getPoolType().getCode()));
            return inserted;
        } catch (Exception e) {
            auditService.fail(tradeDate, table, DAILY, e, Map.of("source", "east_money", "poolType", endpoint.getPoolType().getCode()));
            checkpointService.markFailed(tradeDate, SpiderSourceType.EAST_MONEY.getCode(), table, "DEFAULT", e,
                    Map.of("source", "east_money", "poolType", endpoint.getPoolType().getCode()));
            throw e;
        }
    }

    private List<StockPlateDimensionRow> fetchAllEastMoneyPlates() {
        List<StockPlateDimensionRow> plates = new ArrayList<>();
        for (DcEndpoint endpoint : List.of(DcEndpoint.CONCEPT_PLATE, DcEndpoint.REGION_PLATE, DcEndpoint.INDUSTRY_PLATE)) {
            DcPageResult pageResult = dcApiClient.fetchPaged(endpoint);
            pageResult.rows().stream()
                    .map(node -> fieldMapper.toPlateDimension(endpoint.getPlateType(), node))
                    .forEach(plates::add);
        }
        return plates;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
