/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Dictionary;
import com.example.followup.mapper.DictionaryMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * DictionaryController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dictionaries")
@Api(tags = "数据字典")
public class DictionaryController {
    @Autowired
    private DictionaryMapper dictionaryMapper;

    @GetMapping
    @ApiOperation(value = "按类型查询字典")
    /**
     * 按类型查询字典
     *
     * @param type 参数说明
     * @return 返回值
     */
    public Result<List<Dictionary>> list(@RequestParam String type) {
        return Result.success(dictionaryMapper.selectList(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getDictType, type)
                .eq(Dictionary::getIsActive, 1)
                .orderByAsc(Dictionary::getSortNo)));
    }
}
