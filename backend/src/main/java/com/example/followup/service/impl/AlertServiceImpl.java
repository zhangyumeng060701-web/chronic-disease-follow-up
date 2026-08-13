package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Alert;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PageResponse<AlertVO> listAlerts(AlertQuery query) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        if (query.getAlertType() != null && !query.getAlertType().isEmpty()) {
            wrapper.eq(Alert::getAlertType, query.getAlertType());
        }
        if (query.getAlertLevel() != null && !query.getAlertLevel().isEmpty()) {
            wrapper.eq(Alert::getAlertLevel, query.getAlertLevel());
        }
        if (query.getIsResolved() != null) {
            wrapper.eq(Alert::getIsResolved, query.getIsResolved());
        }
        wrapper.orderByDesc(Alert::getCreateTime);

        Page<Alert> page = new Page<>(query.getPage(), query.getSize());
        alertMapper.selectPage(page, wrapper);

        List<Long> patientIds = page.getRecords().stream()
                .map(Alert::getPatientId).distinct().collect(Collectors.toList());
        Map<Long, String> nameMap = patientIds.isEmpty() ? Map.of() :
                patientMapper.selectBatchIds(patientIds).stream()
                        .collect(Collectors.toMap(Patient::getId, Patient::getName));

        List<AlertVO> vos = page.getRecords().stream().map(a -> {
            AlertVO vo = new AlertVO();
            vo.setId(a.getId());
            vo.setPatientId(a.getPatientId());
            vo.setPatientName(nameMap.getOrDefault(a.getPatientId(), ""));
            vo.setAlertType(a.getAlertType());
            vo.setAlertLevel(a.getAlertLevel());
            vo.setAlertReason(a.getAlertReason());
            vo.setIsResolved(a.getIsResolved());
            vo.setResolveTime(a.getResolveTime());
            vo.setCreateTime(a.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        PageResponse<AlertVO> response = new PageResponse<>();
        response.setRecords(vos);
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    @Override
    public void resolveAlert(Long id) {
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException(404, "预警记录不存在");
        }
        alert.setIsResolved(1);
        alert.setResolveTime(LocalDateTime.now());
        alertMapper.updateById(alert);
    }
}
