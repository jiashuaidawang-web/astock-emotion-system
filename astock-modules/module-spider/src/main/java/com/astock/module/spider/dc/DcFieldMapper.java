package com.astock.module.spider.dc;

import com.astock.module.spider.domain.entity.StockDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDailyKlineRow;
import com.astock.module.spider.domain.entity.StockPlateDimensionRow;
import com.astock.module.spider.domain.entity.StockPlateRelationRow;
import com.astock.module.spider.domain.entity.StockPoolDailySnapshotRow;
import com.astock.module.spider.enums.PoolType;
import com.astock.module.spider.enums.SpiderSourceType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DcFieldMapper {

    private final ObjectMapper objectMapper;

    public DcFieldMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StockDailyKlineRow toStockDailyKline(LocalDate tradeDate, JsonNode node) {
        StockDailyKlineRow row = new StockDailyKlineRow();
        row.setTradeDate(tradeDate);
        row.setStockCode(text(node, "f12"));
        row.setStockName(text(node, "f14"));
        row.setExchange(exchange(node.path("f13").asInt(-1)));
        row.setClosePrice(decimal(node, "f2"));
        row.setPctChange(decimal(node, "f3"));
        row.setChangeAmount(decimal(node, "f4"));
        row.setVolume(longValue(node, "f5") * 100L);
        row.setTurnoverAmount(decimal(node, "f6"));
        row.setAmplitude(decimal(node, "f7"));
        row.setTurnoverRate(decimal(node, "f8"));
        row.setPeDynamic(decimal(node, "f9"));
        row.setVolumeRatio(decimal(node, "f10"));
        row.setHighPrice(decimal(node, "f15"));
        row.setLowPrice(decimal(node, "f16"));
        row.setOpenPrice(decimal(node, "f17"));
        row.setPreClosePrice(decimal(node, "f18"));
        row.setTotalMarketValue(decimal(node, "f20"));
        row.setFloatMarketValue(decimal(node, "f21"));
        row.setPb(decimal(node, "f23"));
        row.setFeaturesMainNetInflow(decimal(node, "f62"));
        row.setFeatures(features(node));
        return row;
    }

    public StockPlateDimensionRow toPlateDimension(int plateType, JsonNode node) {
        StockPlateDimensionRow row = new StockPlateDimensionRow();
        row.setType(SpiderSourceType.EAST_MONEY.getCode());
        row.setPlateType(plateType);
        row.setPlateCode(text(node, "f12"));
        row.setPlateName(text(node, "f14"));
        row.setFeatures(features(node));
        return row;
    }

    public StockPlateDailyKlineRow toPlateDailyKline(LocalDate tradeDate, int plateType, JsonNode node) {
        StockPlateDailyKlineRow row = new StockPlateDailyKlineRow();
        row.setTradeDate(tradeDate);
        row.setType(SpiderSourceType.EAST_MONEY.getCode());
        row.setPlateType(plateType);
        row.setPlateCode(text(node, "f12"));
        row.setPlateName(text(node, "f14"));
        row.setClosePrice(decimal(node, "f2"));
        row.setPctChange(decimal(node, "f3"));
        row.setChangeAmount(decimal(node, "f4"));
        row.setVolume(longValue(node, "f5") * 100L);
        row.setTurnoverAmount(decimal(node, "f6"));
        row.setAmplitude(decimal(node, "f7"));
        row.setTurnoverRate(decimal(node, "f8"));
        row.setVolumeRatio(decimal(node, "f10"));
        row.setHighPrice(decimal(node, "f15"));
        row.setLowPrice(decimal(node, "f16"));
        row.setOpenPrice(decimal(node, "f17"));
        row.setPreClosePrice(decimal(node, "f18"));
        row.setTotalMarketValue(decimal(node, "f20"));
        row.setFloatMarketValue(decimal(node, "f21"));
        row.setFeatures(features(node));
        return row;
    }

    public StockPlateRelationRow toPlateRelation(LocalDate tradeDate, StockPlateDimensionRow plate, JsonNode node) {
        StockPlateRelationRow row = new StockPlateRelationRow();
        row.setTradeDate(tradeDate);
        row.setType(SpiderSourceType.EAST_MONEY.getCode());
        row.setPlateType(plate.getPlateType());
        row.setPlateCode(plate.getPlateCode());
        row.setPlateName(plate.getPlateName());
        row.setStockCode(text(node, "f12"));
        row.setStockName(text(node, "f14"));
        row.setExchange(exchange(node.path("f13").asInt(-1)));
        row.setFeatures(features(node));
        return row;
    }

    public StockPoolDailySnapshotRow toPoolSnapshot(LocalDate tradeDate, PoolType poolType, JsonNode node) {
        StockPoolDailySnapshotRow row = new StockPoolDailySnapshotRow();
        row.setTradeDate(tradeDate);
        row.setPoolType(poolType.getCode());
        row.setStockCode(text(node, "c"));
        row.setStockName(text(node, "n"));
        row.setExchange(exchange(node.path("m").asInt(-1)));
        row.setIndustryName(text(node, "hybk"));
        row.setClosePrice(decimalFen(node, "p"));
        row.setPctChange(decimal(node, "zdp"));
        row.setTurnoverRate(decimal(node, "hs"));
        row.setTotalMarketValue(decimal(node, "tshare"));
        row.setFloatMarketValue(decimal(node, "ltsz"));
        row.setIsLimitUp(poolType == PoolType.LIMIT_UP || poolType == PoolType.YEST_LIMIT_UP ? 1 : 0);
        row.setIsBrokenBoard(poolType == PoolType.BROKEN ? 1 : 0);
        row.setBoardHeight(node.path("lbc").asInt(0));
        row.setLimitUpTime(timeText(node, "fbt"));
        row.setLastLimitUpTime(timeText(node, "lbt"));
        row.setOpenImagesCount(node.path("zbc").asInt(0));
        row.setDaysSinceListed(node.path("ods").asInt(0));
        row.setFeatures(features(node));
        return row;
    }

    private String exchange(int market) {
        return switch (market) {
            case 0 -> "SZ";
            case 1 -> "SH";
            case 90 -> "BK";
            default -> "";
        };
    }

    private BigDecimal decimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || "-".equals(value.asText())) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.asText());
    }

    private BigDecimal decimalFen(JsonNode node, String field) {
        return decimal(node, field).divide(new BigDecimal("1000"));
    }

    private long longValue(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || "-".equals(value.asText())) {
            return 0L;
        }
        return value.asLong(0L);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || "-".equals(value.asText())) {
            return "";
        }
        return value.asText();
    }

    private String timeText(JsonNode node, String field) {
        String value = text(node, field);
        if (value.isBlank() || "0".equals(value)) {
            return "";
        }
        String padded = "000000" + value;
        String time = padded.substring(padded.length() - 6);
        return time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
    }

    private String features(JsonNode node) {
        Map<String, Object> features = new HashMap<>();
        node.fields().forEachRemaining(entry -> features.put(entry.getKey(), entry.getValue()));
        try {
            return objectMapper.writeValueAsString(features);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
