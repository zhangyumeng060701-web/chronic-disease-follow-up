/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.followup.entity.Patient;

import org.apache.ibatis.annotations.Mapper;

/**
 * PatientMapper 数据访问接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
