/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T extends Object> {
    private Integer code;
    private T data;
    private String message;

    /**
     * 执行success操作。
     *
     * @param data 参数说明
     * @return 返回值
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "success");
    }

    /**
    * 执行 success 操作。
    */
    public static <T> Result<T> success() {
        return new Result<>(200, null, "success");
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, null, message);
    }
}
