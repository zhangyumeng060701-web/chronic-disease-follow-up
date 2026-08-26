package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AlertReferRequest {
    @NotBlank(message = "转诊原因不能为空")
    @Size(max = 500, message = "转诊原因不能超过500字")
    private String referralReason;
}
