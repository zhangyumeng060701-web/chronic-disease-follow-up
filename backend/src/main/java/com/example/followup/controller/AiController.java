package com.example.followup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, String> FIELD_RENAMES = Map.of(
            "files_to_modify", "filesToModify",
            "api_endpoint", "apiEndpoint",
            "acceptance_criteria", "acceptanceCriteria"
    );

    @Value("${agent.arts.api-key:}")
    private String agentArtsApiKey;

    @Value("${agent.arts.session-id:}")
    private String agentArtsSessionId;

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        if (requirement == null || requirement.trim().isEmpty()) {
            return Map.of("code", 400, "message", "需求文本不能为空");
        }
        if (!StringUtils.hasText(agentArtsApiKey)) {
            return Map.of("code", 500, "message", "AGENT_ARTS_API_KEY 未配置，请通过环境变量注入");
        }

        try {
            log.info("开始拉起真实 AI 链路，需求内容: {}", requirement);
            String scriptPath = new File("../ai-agent/scripts/decompose.py").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath, requirement);
            Map<String, String> env = pb.environment();
            env.put("AGENT_ARTS_API_KEY", agentArtsApiKey);
            if (StringUtils.hasText(agentArtsSessionId)) {
                env.put("AGENT_ARTS_SESSION_ID", agentArtsSessionId);
            }
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(50, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return Map.of("code", 504, "message", "AI 思考超时，请稍后重试");
            }

            String pythonOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .trim();
            log.info("AI 脚本返回原始数据: {}", pythonOutput);

            if (process.exitValue() != 0 || !pythonOutput.startsWith("{")) {
                return Map.of("code", 500, "message", "AI 引擎执行异常", "detail", pythonOutput);
            }

            Object jsonData = objectMapper.readValue(pythonOutput, Object.class);
            jsonData = normalizeKeys(jsonData);
            return Map.of("code", 200, "message", "success", "data", jsonData);
        } catch (Exception e) {
            log.error("后端处理 AI 请求失败", e);
            return Map.of("code", 500, "message", "后端解析异常: " + e.getMessage());
        }
    }

    private Object normalizeKeys(Object value) {
        if (value instanceof Map) {
            Map<?, ?> source = (Map<?, ?>) value;
            Map<String, Object> result = new LinkedHashMap<>();
            source.forEach((key, item) -> {
                String normalizedKey = FIELD_RENAMES.getOrDefault(String.valueOf(key), String.valueOf(key));
                result.put(normalizedKey, normalizeKeys(item));
            });
            return result;
        }
        if (value instanceof List) {
            List<Object> result = new ArrayList<>();
            ((List<?>) value).forEach(item -> result.add(normalizeKeys(item)));
            return result;
        }
        return value;
    }
}
