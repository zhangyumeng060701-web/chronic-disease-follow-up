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
import java.io.IOException;
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
            log.info("Starting real AI invocation, requirement: {}", requirement);
            File scriptFile = new File("/root/ai-decompose.py");
            if (!scriptFile.exists()) {
                scriptFile = new File("../ai-agent/scripts/decompose.py");
            }
            if (!scriptFile.exists()) {
                scriptFile = new File("/root/chronic-disease-follow-up/ai-agent/scripts/decompose.py");
            }
            String scriptPath = resolveScriptPath(scriptFile);
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath, requirement);
            Map<String, String> env = pb.environment();
            env.put("AGENT_ARTS_API_KEY", agentArtsApiKey);
            env.put("AGENT_API_KEY", agentArtsApiKey);
            env.put("AGENT_ARTS_PROMPT_PATH", "/root/ai-prompt.txt");
            if (StringUtils.hasText(agentArtsSessionId)) {
                env.put("AGENT_ARTS_SESSION_ID", agentArtsSessionId);
                env.put("AGENT_SESSION_ID", agentArtsSessionId);
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
            log.info("AI script returned raw data: {}", pythonOutput);

            if (process.exitValue() != 0 || !pythonOutput.startsWith("{")) {
                return Map.of("code", 500, "message", "AI 引擎执行异常", "detail", pythonOutput);
            }

            Object jsonData = objectMapper.readValue(pythonOutput, Object.class);
            jsonData = normalizeKeys(jsonData);
            return Map.of("code", 200, "message", "success", "data", jsonData);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Backend failed to process AI request", e);
            return Map.of("code", 500, "message", "后端解析异常: " + e.getMessage());
        }
    }

    private String resolveScriptPath(File scriptFile) throws IOException {
        String canonicalPath = scriptFile.getCanonicalPath();
        if (!canonicalPath.equals("/root/ai-decompose.py")
                && !canonicalPath.endsWith("/ai-agent/scripts/decompose.py")) {
            throw new IllegalStateException("Unsafe AI script path: " + canonicalPath);
        }
        return canonicalPath;
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
