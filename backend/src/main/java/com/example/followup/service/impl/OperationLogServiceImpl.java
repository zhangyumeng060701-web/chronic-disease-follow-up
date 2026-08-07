package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.OperationLog;
import com.example.followup.mapper.OperationLogMapper;
import com.example.followup.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public PageResponse<OperationLog> listLogs(LogQuery query) {
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

        PageResponse<OperationLog> response = new PageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    @Override
    public void log(String username, String operation, String targetType, Long targetId) {
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setIpAddress("127.0.0.1");
        operationLogMapper.insert(log);
    }
}
