/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public final class PageResponseUtil {

    private PageResponseUtil() {
    }

/**
 * 执行 of 操作。
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
