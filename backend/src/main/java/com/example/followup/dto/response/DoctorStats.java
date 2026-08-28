/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DoctorStats 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStats {
    private Long doctorId;
    private String doctorName;
    private Long patientCount;
    private String completionRate;
    private Long highRiskCount;
}
