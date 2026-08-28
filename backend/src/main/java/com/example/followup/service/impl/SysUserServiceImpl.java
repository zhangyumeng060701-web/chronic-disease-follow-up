/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.dto.response.UserVO;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.service.SysUserService;
import com.example.followup.util.VoMappers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * SysUserServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    /**
     * 查询listUsers。
     *
     * @param query 参数说明
     * @return 返回值
     */
    public PageResponse<UserVO> listUsers(UserQuery query) {
        long start = System.currentTimeMillis();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getUsername())) {
            wrapper.like(SysUser::getUsername, query.getUsername());
        }
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq(SysUser::getRole, query.getRole());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        sysUserMapper.selectPage(page, wrapper);

        return PageResponseUtil.of(
                page,
                page.getRecords().stream().map(VoMappers::toUserVO).collect(Collectors.toList()),
                query.getPage(),
                query.getSize()
        );
    }

/**
 * 执行 createUser 操作。
 */
    @Override
    public void createUser(CreateUserRequest request) {
        long start = System.currentTimeMillis();
        SysUser existing = sysUserMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        sysUserMapper.insert(user);
        log.info("createUser username={} cost={}ms", request.getUsername(), System.currentTimeMillis() - start);
    }

/**
 * 执行 updateUser 操作。
 */
    @Override
    public void updateUser(Long id, UpdateUserRequest request) {
        long start = System.currentTimeMillis();
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        existing.setRealName(request.getRealName());
        existing.setRole(request.getRole());
        existing.setPhone(request.getPhone());
        if (StringUtils.hasText(request.getPassword())) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        sysUserMapper.updateById(existing);
        log.info("updateUser id={} cost={}ms", id, System.currentTimeMillis() - start);
    }

/**
 * 执行 toggleUserStatus 操作。
 */
    @Override
    public void toggleUserStatus(Long id) {
        long start = System.currentTimeMillis();
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        sysUserMapper.updateById(user);
        log.info("toggleUserStatus id={} cost={}ms", id, System.currentTimeMillis() - start);
    }
}
