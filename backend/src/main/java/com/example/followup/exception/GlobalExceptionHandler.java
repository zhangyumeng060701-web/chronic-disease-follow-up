/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.exception;

import com.example.followup.dto.response.Result;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * 执行handleValidation操作。
     *
     * @param e 参数说明
     * @return 返回值
     */
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Result.error(ErrorCode.BAD_REQUEST.getHttpStatus(), msg);
    }

/**
 * 执行 handleUnknown 操作。
 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception e) {
        log.error("Unknown error", e);
        return Result.error(ErrorCode.INTERNAL_ERROR.getHttpStatus(), ErrorCode.INTERNAL_ERROR.getDefaultMessage());
    }
}
