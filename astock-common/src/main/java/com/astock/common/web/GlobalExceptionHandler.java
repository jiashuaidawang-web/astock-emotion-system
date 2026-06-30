package com.astock.common.web;

import com.astock.common.api.ApiResult;
import com.astock.common.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException ex) {
        return ApiResult.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception ex) {
        return ApiResult.fail("SYSTEM_ERROR", ex.getMessage());
    }
}
