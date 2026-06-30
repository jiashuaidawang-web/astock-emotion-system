package com.astock.common.convert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Map 字段读取工具。
 *
 * <p>用于将 JDBC、MyBatis、ClickHouse 查询结果中的 Map 字段安全转换为业务字段，
 * 统一处理空值、数字、布尔、日期、时间等类型转换，避免各个 Converter 中重复编写转换逻辑。</p>
 *
 * <p>注意：本类保留 string、decimal、integer、bool、localDate 等短方法名，
 * 用于兼容现有 Converter 调用，不改变现有业务逻辑。</p>
 */
public final class MapFieldReader {

    /**
     * 工具类禁止实例化。
     */
    private MapFieldReader() {
    }

    /**
     * 读取字符串字段，兼容现有 Converter 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return 字符串值，空值返回 null
     */
    public static String string(Map<String, Object> row, String key) {
        return stringValue(row, key);
    }

    /**
     * 读取字符串字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return 字符串值，空值返回 null
     */
    public static String stringValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 读取 Integer 字段，兼容现有 Converter 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Integer 值，空值返回 null
     */
    public static Integer integer(Map<String, Object> row, String key) {
        return intValue(row, key);
    }

    /**
     * 读取 Integer 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Integer 值，空值返回 null
     */
    public static Integer intValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            return Integer.valueOf(number.intValue());
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Integer.valueOf(text);
    }

    /**
     * 读取 Long 字段，兼容可能存在的 long 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Long 值，空值返回 null
     */
    public static Long longNumber(Map<String, Object> row, String key) {
        return longValue(row, key);
    }

    /**
     * 读取 Long 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Long 值，空值返回 null
     */
    public static Long longValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            return Long.valueOf(number.longValue());
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Long.valueOf(text);
    }

    /**
     * 读取 BigDecimal 字段，兼容现有 Converter 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return BigDecimal 值，空值返回 null
     */
    public static BigDecimal decimal(Map<String, Object> row, String key) {
        return decimalValue(row, key);
    }

    /**
     * 读取 BigDecimal 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return BigDecimal 值，空值返回 null
     */
    public static BigDecimal decimalValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            return BigDecimal.valueOf(number.doubleValue());
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : new BigDecimal(text);
    }

    /**
     * 读取 Boolean 字段，兼容现有 Converter 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Boolean 值，空值返回 null
     */
    public static Boolean bool(Map<String, Object> row, String key) {
        return booleanValue(row, key);
    }

    /**
     * 读取 Boolean 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return Boolean 值，空值返回 null
     */
    public static Boolean booleanValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            return number.intValue() != 0;
        }

        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }

        return "true".equalsIgnoreCase(text)
                || "1".equals(text)
                || "yes".equalsIgnoreCase(text)
                || "y".equalsIgnoreCase(text);
    }

    /**
     * 读取 LocalDate 字段，兼容现有 Converter 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return LocalDate 值，空值返回 null
     */
    public static LocalDate localDate(Map<String, Object> row, String key) {
        return dateValue(row, key);
    }

    /**
     * 读取 LocalDate 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return LocalDate 值，空值返回 null
     */
    public static LocalDate dateValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            java.sql.Date sqlDate = (java.sql.Date) value;
            return sqlDate.toLocalDate();
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            return localDateTime.toLocalDate();
        }
        if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            return timestamp.toLocalDateTime().toLocalDate();
        }

        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : LocalDate.parse(text.substring(0, 10));
    }

    /**
     * 读取 LocalDateTime 字段，兼容可能存在的 localDateTime 调用。
     *
     * @param row 数据行
     * @param key 字段名
     * @return LocalDateTime 值，空值返回 null
     */
    public static LocalDateTime localDateTime(Map<String, Object> row, String key) {
        return dateTimeValue(row, key);
    }

    /**
     * 读取 LocalDateTime 字段。
     *
     * @param row 数据行
     * @param key 字段名
     * @return LocalDateTime 值，空值返回 null
     */
    public static LocalDateTime dateTimeValue(Map<String, Object> row, String key) {
        if (row == null || key == null) {
            return null;
        }
        Object value = row.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            return timestamp.toLocalDateTime();
        }

        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(text.replace(" ", "T"));
    }
}