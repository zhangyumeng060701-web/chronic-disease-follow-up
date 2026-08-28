/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

import java.util.List;

/**
 * PageResponse 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class PageResponse<T extends Object> {
    private List<T> records;
    private long total;
    private long page;
    private long size;

    /**
     * 从 MyBatis-Plus Page 和查询参数构建统一的 PageResponse。
     */
    public static <T> PageResponse<T> of(Page<T> mpPage, long pageNum, long pageSize) {
        PageResponse<T> resp = new PageResponse<>();
        resp.setRecords(mpPage.getRecords());
        resp.setTotal(mpPage.getTotal());
        resp.setPage(pageNum);
        resp.setSize(pageSize);
        return resp;
    }
}
