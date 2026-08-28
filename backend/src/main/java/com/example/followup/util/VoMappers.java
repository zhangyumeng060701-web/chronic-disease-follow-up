/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.util;

import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.dto.response.UserVO;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;

import org.springframework.beans.BeanUtils;

/**
 * VoMappers 业务组件。
 *
 * @since 2026-08-28
 */
public final class VoMappers {
    private VoMappers() {
    }

    /**
    * 执行 toPatientVO 操作。
    */
    public static PatientVO toPatientVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        return vo;
    }

    public static UserVO toUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /**
    * 执行 toFollowUpVO 操作。
    */
    public static FollowUpVO toFollowUpVO(FollowUp followUp) {
        FollowUpVO vo = new FollowUpVO();
        BeanUtils.copyProperties(followUp, vo);
        return vo;
    }
}
