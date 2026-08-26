package com.example.followup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class QuestionnaireSubmitRequest {
    @NotNull(message = "答案不能为空")
    private Map<String, Object> answers;
}
