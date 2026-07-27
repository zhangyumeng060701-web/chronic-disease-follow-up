package com.example.followup.dto.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

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
