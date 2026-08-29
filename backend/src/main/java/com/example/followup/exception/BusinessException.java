/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.exception;

/**
 * BusinessException 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * 执行BusinessException操作。
     *
     * @param errorCode 参数说明
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * 执行BusinessException操作。
     *
     * @param errorCode 参数说明
     * @param message 参数说明
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 查询getErrorCode。
     *
     * @return 返回值
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 查询getHttpStatus。
     *
     * @return 返回值
     */
    public int getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
