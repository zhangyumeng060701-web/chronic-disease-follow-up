package com.example.followup.dto.request;

import lombok.Data;

import java.util.Map;

import javax.validation.constraints.NotNull;

@Data
public class QuestionnaireSubmitRequest {
    @NotNull(message = "答案不能为空")
    private Map<String, Object> answers;
}
