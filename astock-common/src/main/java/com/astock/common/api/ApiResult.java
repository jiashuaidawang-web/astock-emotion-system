package com.astock.common.api;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * ApiResult 数据载体。
 */
@Data
public class ApiResult<T> {
    /** 是否成功。 */
    private Boolean success;
    /** 编码。 */
    private String code;
    /** message 字段。 */
    private String message;
    /** 数据体。 */
    private T data;
    /** traceId 字段。 */
    private String traceId;
    /** 服务端时间。 */
    private LocalDateTime serverTime;

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.success = true;
        result.code = "SUCCESS";
        result.message = "成功";
        result.data = data;
        result.serverTime = LocalDateTime.now();
        return result;
    }

    public static ApiResult<Void> success() {
        return success(null);
    }

    public static <T> ApiResult<T> fail(String code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.success = false;
        result.code = code;
        result.message = message;
        result.serverTime = LocalDateTime.now();
        return result;
    }
}
