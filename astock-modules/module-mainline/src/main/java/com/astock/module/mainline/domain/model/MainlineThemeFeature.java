package com.astock.module.mainline.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Data;

/**
 * MainlineThemeFeature 数据载体。
 */
@Data
public class MainlineThemeFeature {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 市场范围。 */
    private String marketScope;
    /** themeCode 字段。 */
    private String themeCode;
    /** themeName 字段。 */
    private String themeName;
    /** themeType 字段。 */
    private String themeType;
    /** pctChange 字段。 */
    private BigDecimal pctChange;
    /** limitUpCount 字段。 */
    private BigDecimal limitUpCount;
    /** stockCount 字段。 */
    private BigDecimal stockCount;
    /** turnoverAmount 字段。 */
    private BigDecimal turnoverAmount;
    /** turnoverRatio 字段。 */
    private BigDecimal turnoverRatio;
    /** continuityDays 字段。 */
    private BigDecimal continuityDays;
    /** maxBoardHeight 字段。 */
    private BigDecimal maxBoardHeight;
    /** leaderCount 字段。 */
    private BigDecimal leaderCount;
    /** leaderDriveRawScore 字段。 */
    private BigDecimal leaderDriveRawScore;
    /** emotionStage 字段。 */
    private String emotionStage;
    /** sourceRow 字段。 */
    private Map<String, Object> sourceRow;
}
