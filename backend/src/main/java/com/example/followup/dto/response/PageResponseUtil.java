/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * PageResponseUtil 工具类。
 *
 * @since 2026-08-28
 */
public final class PageResponseUtil {
    private PageResponseUtil() {
    }

    /**
     * 执行of操作。
     *
     * @param page 参数说明
     * @param records 参数说明
     * @param pageNum 参数说明
     * @param pageSize 参数说明
     * @return 返回值
     */
    public static <T, R> PageResponse<R> of(Page<T> page, List<R> records, long pageNum, long pageSize) {
        PageResponse<R> response = new PageResponse<>();
        response.setRecords(records);
        response.setTotal(page.getTotal());
        response.setPage(pageNum);
        response.setSize(pageSize);
        return response;
    }
}
