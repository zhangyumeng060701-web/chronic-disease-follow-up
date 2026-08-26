package com.example.followup.service;

import com.example.followup.controller.AiController;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiServiceTest {
    private final AiController aiController = new AiController();

    @Test
    void missingCredentialFailsClosedWithoutStartingExternalProcess() {
        Map<String, Object> result = aiController.decompose(
                Map.of("requirement", "测试需求：增加一个导出按钮"));

        assertNotNull(result);
        assertEquals(500, result.get("code"));
    }

    @Test
    void emptyRequirementIsRejected() {
        Map<String, Object> result = aiController.decompose(Map.of("requirement", ""));

        assertEquals(400, result.get("code"));
    }
}
