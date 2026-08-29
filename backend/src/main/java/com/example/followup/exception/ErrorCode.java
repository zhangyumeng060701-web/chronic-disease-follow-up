/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.exception;

/**
 * 业务异常码枚举，统一管理 HTTP 状态码与业务含义。
 *
 * @since 2026-08-28
 */
public enum ErrorCode {
    // ---- 通用 ----
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ---- 业务 ----
    USER_NOT_FOUND(404, "用户不存在"),
    USER_DISABLED(403, "账号已被禁用"),
    USER_PASSWORD_WRONG(401, "用户名或密码错误"),
    PATIENT_NOT_FOUND(404, "患者不存在"),
    FOLLOWUP_NOT_FOUND(404, "随访记录不存在"),
    ALERT_NOT_FOUND(404, "预警记录不存在");

    private final int httpStatus;
    private final String defaultMessage;

    ErrorCode(int httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    /**
     * 执行 getHttpStatus 操作。
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
