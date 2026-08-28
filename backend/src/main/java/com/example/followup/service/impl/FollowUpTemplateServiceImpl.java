/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.FollowUpTemplateQuery;
import com.example.followup.dto.response.FollowUpTemplateVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.FollowUpTemplate;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.FollowUpTemplateMapper;
import com.example.followup.service.FollowUpTemplateService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FollowUpTemplateServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class FollowUpTemplateServiceImpl implements FollowUpTemplateService {

    @Autowired
    private FollowUpTemplateMapper templateMapper;

    @Override
    public PageResponse<FollowUpTemplateVO> listTemplates(FollowUpTemplateQuery query) {
        long start = System.currentTimeMillis();
        LambdaQueryWrapper<FollowUpTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getTemplateCode())) {
            wrapper.like(FollowUpTemplate::getTemplateCode, query.getTemplateCode());
        }
        if (query.getIsActive() != null) {
            wrapper.eq(FollowUpTemplate::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(FollowUpTemplate::getCreateTime);

        Page<FollowUpTemplate> page = new Page<>(query.getPage(), query.getSize());
        templateMapper.selectPage(page, wrapper);
        List<FollowUpTemplateVO> vos = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        log.info("listTemplates total={} cost={}ms", page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

/**
 * 执行 createTemplate 操作。
 */
    @Override
    public FollowUpTemplateVO createTemplate(FollowUpTemplate template) {
        if (StringUtils.hasText(template.getTemplateCode())) {
            Long count = templateMapper.selectCount(new LambdaQueryWrapper<FollowUpTemplate>()
                    .eq(FollowUpTemplate::getTemplateCode, template.getTemplateCode()));
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "模板编码已存在");
            }
        }
        if (template.getIsActive() == null) {
            template.setIsActive(1);
        }
        templateMapper.insert(template);
        log.info("createTemplate id={} code={}", template.getId(), template.getTemplateCode());
        return toVO(template);
    }

/**
 * 执行 updateTemplate 操作。
 */
    @Override
    public FollowUpTemplateVO updateTemplate(Long id, FollowUpTemplate template) {
        FollowUpTemplate existing = getTemplateOrThrow(id);
        existing.setTemplateCode(template.getTemplateCode());
        existing.setTemplateName(template.getTemplateName());
        existing.setRiskLevel(template.getRiskLevel());
        existing.setFrequencyDays(template.getFrequencyDays());
        existing.setFollowUpType(template.getFollowUpType());
        existing.setDefaultContent(template.getDefaultContent());
        if (template.getIsActive() != null) {
            existing.setIsActive(template.getIsActive());
        }
        templateMapper.updateById(existing);
        log.info("updateTemplate id={}", id);
        return toVO(existing);
    }

/**
 * 执行 toggleTemplate 操作。
 */
    @Override
    public void toggleTemplate(Long id) {
        FollowUpTemplate template = getTemplateOrThrow(id);
        template.setIsActive(template.getIsActive() != null && template.getIsActive() == 1 ? 0 : 1);
        templateMapper.updateById(template);
        log.info("toggleTemplate id={} active={}", id, template.getIsActive());
    }

    private FollowUpTemplate getTemplateOrThrow(Long id) {
        FollowUpTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "随访模板不存在");
        }
        return template;
    }

    private FollowUpTemplateVO toVO(FollowUpTemplate template) {
        FollowUpTemplateVO vo = new FollowUpTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setRiskLevel(template.getRiskLevel());
        vo.setFrequencyDays(template.getFrequencyDays());
        vo.setFollowUpType(template.getFollowUpType());
        vo.setDefaultContent(template.getDefaultContent());
        vo.setIsActive(template.getIsActive());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}
