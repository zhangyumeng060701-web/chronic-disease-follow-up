/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.OperationLog;
import com.example.followup.mapper.OperationLogMapper;
import com.example.followup.service.OperationLogService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * OperationLogServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public PageResponse<OperationLog> listLogs(LogQuery query) {
        long start = System.currentTimeMillis();
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(OperationLog::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getOperation())) {
            wrapper.like(OperationLog::getOperation, query.getOperation());
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> page = new Page<>(query.getPage(), query.getSize());
        operationLogMapper.selectPage(page, wrapper);

        log.info("listLogs total={} cost={}ms", page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, page.getRecords(), query.getPage(), query.getSize());
    }

    /**
     * 执行 log 操作。
     */
    @Override
    public void log(Long userId, String username, String operation, String targetType, Long targetId, String detail) {
        long start = System.currentTimeMillis();
        OperationLog entity = new OperationLog();
        entity.setUserId(userId);
        entity.setUsername(username);
        entity.setOperation(operation);
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setDetail(detail);
        entity.setIpAddress(currentIp());
        operationLogMapper.insert(entity);
        log.info("operationLog userId={} operation={} cost={}ms", userId, operation, System.currentTimeMillis() - start);
    }

    private String currentIp() {
        Object attributesObject = RequestContextHolder.getRequestAttributes();
        if (!(attributesObject instanceof ServletRequestAttributes)) {
            return "unknown";
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) attributesObject;
        HttpServletRequest request = attributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
