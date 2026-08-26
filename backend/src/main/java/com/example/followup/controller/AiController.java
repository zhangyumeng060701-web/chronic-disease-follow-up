package com.example.followup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        if (requirement == null || requirement.trim().isEmpty()) {
            return Map.of("code", 400, "message", "需求文本不能为空");
        }
        try {
            String apiKey = System.getenv("AGENT_ARTS_API_KEY");
            String sessionId = System.getenv("AGENT_ARTS_SESSION_ID");
            if (apiKey == null || apiKey.isBlank()) {
                return Map.of("code", 500, "message", "服务器环境配置错误：缺失 AI 凭证");
            }
            ProcessBuilder builder = new ProcessBuilder("python3", "../ai-agent/scripts/decompose.py", requirement);
            builder.environment().put("AGENT_ARTS_API_KEY", apiKey);
            if (sessionId != null && !sessionId.isBlank()) {
                builder.environment().put("AGENT_ARTS_SESSION_ID", sessionId);
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            boolean finished = process.waitFor(50, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return Map.of("code", 504, "message", "AI 思考超时，请尝试精简需求描述");
            }
            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = reader.lines().collect(Collectors.joining("\n")).trim();
            }
            if (process.exitValue() != 0 || !output.startsWith("{")) {
                return Map.of("code", 500, "message", "AI 引擎执行异常", "detail", output);
            }
            return Map.of("code", 200, "message", "success", "data", objectMapper.readValue(output, Object.class));
        } catch (Exception exception) {
            log.error("AI 后端解析异常", exception);
            return Map.of("code", 500, "message", "后端解析异常: " + exception.getMessage());
        }
    }
}
