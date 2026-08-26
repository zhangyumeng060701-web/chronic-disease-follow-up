package com.example.followup.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 3号 AI 需求拆解核心接口 - 生产级收口版
 * 功能：实现了源码密钥脱敏、Jackson 对象化解析、长超时控制
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin // 支持前端跨域联调
public class AiController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        
        if (requirement == null || requirement.trim().isEmpty()) {
            return Map.of("code", 400, "message", "需求文本不能为空");
        }

        try {
            log.info("【AI链路】接收到联调请求，开始调度 AgentArts...");

            // 1. 获取服务器环境变量 (密钥不再写死在代码里)
            // 请确保 ECS 服务器已执行: export AGENT_API_KEY=xxx
            String apiKey = System.getenv("AGENT_API_KEY");
            String sessionId = System.getenv("AGENT_SESSION_ID");

            if (apiKey == null) {
                log.error("【安全报错】服务器未配置环境变量 AGENT_API_KEY");
                return Map.of("code", 500, "message", "服务器环境配置错误：缺失 AI 凭证");
            }

            // 2. 脚本路径（基于 backend 运行目录）
            String scriptPath = "../ai-agent/scripts/decompose.py";
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath, requirement);
            
            // 3. 将环境变量注入子进程
            Map<String, String> env = pb.environment();
            env.put("AGENT_API_KEY", apiKey);
            if (sessionId != null) env.put("AGENT_SESSION_ID", sessionId);

            pb.redirectErrorStream(true); 
            Process process = pb.start();

            // 4. 读取脚本输出结果 (UTF-8 编码)
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            // 5. 超时控制：设定 50 秒（盘古 Reasoner 模型思考深度较大）
            boolean finished = process.waitFor(50, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("【AI链路】Agent 响应超时");
                return Map.of("code", 504, "message", "AI 思考超时，请尝试精简需求描述");
            }

            // 获取 Python 打印的 JSON 字符串
            String pythonOutput = reader.lines().collect(Collectors.joining("\n")).trim();
            
            // 6. 验证返回内容
            if (process.exitValue() != 0 || !pythonOutput.startsWith("{")) {
                log.error("【AI链路】脚本报错或非 JSON 输出: {}", pythonOutput);
                return Map.of("code", 500, "message", "AI 引擎执行异常", "detail", pythonOutput);
            }

            // --- 核心质量收口：将字符串解析为 Java 对象 ---
            // 解决 4 号反馈：确保 data 字段是一个真正的 JSON 对象，而非字符串
            Object jsonData = objectMapper.readValue(pythonOutput, Object.class);

            log.info("【AI链路】联调成功，数据已完成对象化封装。");
            return Map.of(
                "code", 200,
                "message", "success",
                "data", jsonData
            );

        } catch (Exception e) {
            log.error("【AI链路】后端解析异常", e);
            return Map.of("code", 500, "message", "后端解析异常: " + e.getMessage());
        }
    }
}

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @PostMapping("/decompose")
    public String decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        if (requirement == null || requirement.trim().isEmpty()) {
            return "{\"code\":400,\"message\":\"需求文本不能为空\"}";
        }

        try {
            String scriptPath = new File("../ai-agent/scripts/decompose.py").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, requirement);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return "{\"code\":504,\"message\":\"AI 响应超时\"}";
            }

            String result;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                result = reader.lines().collect(Collectors.joining("\n"));
            }

            if (process.exitValue() != 0) {
                String detail = result.replace("\"", "'");
                return "{\"code\":500,\"message\":\"脚本执行失败\",\"detail\":\"" + detail + "\"}";
            }

            return result;
        } catch (Exception e) {
            return "{\"code\":500,\"message\":\"后端调用异常:" + e.getMessage() + "\"}";
        }
    }
}

