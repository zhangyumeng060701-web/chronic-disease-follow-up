/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;

/**
 * PatientService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface PatientService {
    /**
     * 查询listPatients。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<PatientVO> listPatients(PatientQuery query);
    /**
     * 查询getPatientById。
     *
     * @param id 参数说明
     * @return 返回值
     */
    PatientVO getPatientById(Long id);
    /**
     * 新增addPatient。
     *
     * @param request 参数说明
     */
    void addPatient(PatientSaveRequest request);
    /**
     * 更新updatePatient。
     *
     * @param id 参数说明
     * @param request 参数说明
     */
    void updatePatient(Long id, PatientUpdateRequest request);
    /**
     * 删除deletePatient。
     *
     * @param id 参数说明
     */
    void deletePatient(Long id);
    /**
     * 导出exportPatientsCsv。
     *
     * @return 返回值
     */
    String exportPatientsCsv();
}
