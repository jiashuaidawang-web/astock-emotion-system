package com.astock.module.spider.service.impl;

import com.astock.module.spider.domain.entity.StockDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDimensionRow;
import com.astock.module.spider.domain.entity.StockPlateRelationRow;
import com.astock.module.spider.domain.entity.StockPoolDailySnapshotRow;
import com.astock.module.spider.service.SpiderClickHouseRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SpiderClickHouseRepositoryImpl implements SpiderClickHouseRepository {

    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    public SpiderClickHouseRepositoryImpl(
            @Qualifier("clickHouseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate clickHouseJdbcTemplate) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
    }

    @Override
    public int insertStockDailyKline(List<StockDailyKlineRow> rows) {
        String sql = """
                INSERT INTO astock_analysis.stock_daily_kline
                (trade_date, stock_code, stock_name, exchange, open_price, high_price, low_price, close_price,
                 pre_close_price, pct_change, change_amount, volume, turnover_amount, amplitude, volume_ratio,
                 turnover_rate, pe_dynamic, pb, total_market_value, float_market_value, features)
                VALUES
                (:tradeDate, :stockCode, :stockName, :exchange, :openPrice, :highPrice, :lowPrice, :closePrice,
                 :preClosePrice, :pctChange, :changeAmount, :volume, :turnoverAmount, :amplitude, :volumeRatio,
                 :turnoverRate, :peDynamic, :pb, :totalMarketValue, :floatMarketValue, :features)
                """;
        return batch(sql, rows);
    }

    @Override
    public int insertPlateDimensions(List<StockPlateDimensionRow> rows) {
        String sql = """
                INSERT INTO astock_analysis.stock_plate_dimension
                (type, plate_type, plate_code, plate_name, features)
                VALUES (:type, :plateType, :plateCode, :plateName, :features)
                """;
        return batch(sql, rows);
    }

    @Override
    public int insertPlateDailyKline(List<StockPlateDailyKlineRow> rows) {
        String sql = """
                INSERT INTO astock_analysis.stock_plate_daily_kline
                (trade_date, type, plate_type, plate_code, plate_name, close_price, open_price, high_price, low_price,
                 pre_close_price, pct_change, change_amount, volume, turnover_amount, amplitude, volume_ratio,
                 turnover_rate, total_market_value, float_market_value, features)
                VALUES
                (:tradeDate, :type, :plateType, :plateCode, :plateName, :closePrice, :openPrice, :highPrice, :lowPrice,
                 :preClosePrice, :pctChange, :changeAmount, :volume, :turnoverAmount, :amplitude, :volumeRatio,
                 :turnoverRate, :totalMarketValue, :floatMarketValue, :features)
                """;
        return batch(sql, rows);
    }

    @Override
    public int insertPlateRelations(List<StockPlateRelationRow> rows) {
        String sql = """
                INSERT INTO astock_analysis.stock_plate_relation
                (trade_date, type, plate_type, plate_code, plate_name, stock_code, stock_name, exchange, features)
                VALUES
                (:tradeDate, :type, :plateType, :plateCode, :plateName, :stockCode, :stockName, :exchange, :features)
                """;
        return batch(sql, rows);
    }

    @Override
    public int insertPoolSnapshots(List<StockPoolDailySnapshotRow> rows) {
        String sql = """
                INSERT INTO astock_analysis.stock_pool_daily_snapshot
                (trade_date, pool_type, stock_code, stock_name, exchange, industry_name, close_price, pct_change,
                 turnover_rate, total_market_value, float_market_value, is_limit_up, is_broken_board, board_height,
                 limit_up_time, last_limit_up_time, open_images_count, days_since_listed, features)
                VALUES
                (:tradeDate, :poolType, :stockCode, :stockName, :exchange, :industryName, :closePrice, :pctChange,
                 :turnoverRate, :totalMarketValue, :floatMarketValue, :isLimitUp, :isBrokenBoard, :boardHeight,
                 :limitUpTime, :lastLimitUpTime, :openImagesCount, :daysSinceListed, :features)
                """;
        return batch(sql, rows);
    }

    @Override
    public Map<String, Object> validateDaily(LocalDate tradeDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("dailyKlineStockCount", count("stock_daily_kline", "trade_date = :tradeDate", tradeDate));
        result.put("eastMoneyPlateCount", count("stock_plate_dimension", "type = 1", tradeDate));
        result.put("eastMoneyPlateDailyCount", count("stock_plate_daily_kline", "trade_date = :tradeDate AND type = 1", tradeDate));
        result.put("eastMoneyRelationCount", count("stock_plate_relation", "trade_date = :tradeDate AND type = 1", tradeDate));
        result.put("emptyEastMoneyPlateCount", queryEmptyPlateCount(tradeDate));
        return result;
    }

    private int count(String table, String where, LocalDate tradeDate) {
        String sql = "SELECT count() FROM astock_analysis." + table + " WHERE " + where;
        return clickHouseJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("tradeDate", tradeDate), Integer.class);
    }

    private int queryEmptyPlateCount(LocalDate tradeDate) {
        String sql = """
                SELECT count()
                FROM astock_analysis.stock_plate_daily_kline p
                LEFT JOIN
                (
                    SELECT plate_code, plate_type, count() cnt
                    FROM astock_analysis.stock_plate_relation
                    WHERE trade_date = :tradeDate AND type = 1
                    GROUP BY plate_code, plate_type
                ) r ON p.plate_code = r.plate_code AND p.plate_type = r.plate_type
                WHERE p.trade_date = :tradeDate AND p.type = 1 AND ifNull(r.cnt, 0) = 0
                """;
        return clickHouseJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("tradeDate", tradeDate), Integer.class);
    }

    private <T> int batch(String sql, List<T> rows) {
        if (rows == null || rows.isEmpty()) {
            return 0;
        }
        BeanPropertySqlParameterSource[] params = rows.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(BeanPropertySqlParameterSource[]::new);
        int[] results = clickHouseJdbcTemplate.batchUpdate(sql, params);
        int count = 0;
        for (int result : results) {
            count += Math.max(result, 0);
        }
        return count == 0 ? rows.size() : count;
    }
}
