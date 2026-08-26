package com.example.followup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 3号 AI 任务拆解核心接口
 * 作用：接收前端需求 -> 拉起 Python 脚本调盘古 -> 将结果解析为 JSON 对象回传
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin // 必须允许跨域，方便 4 号前端联调
public class AiController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        
        if (requirement == null || requirement.trim().isEmpty()) {
            return Map.of("code", 400, "message", "需求文本不能为空");
        }

        try {
            log.info("开始拉起真实 AI 链路，需求内容: {}", requirement);

            // 1. 脚本路径（基于 backend 运行目录）
            String scriptPath = "../ai-agent/scripts/decompose.py";
            
            // 2. 构造进程：执行 python3 ai-agent/scripts/decompose.py "需求"
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath, requirement);
            
            // 3. 注入环境变量（安全脱敏的关键：Key 在 Java 注入，不在 Python 写死）
            Map<String, String> env = pb.environment();
            env.put("AGENT_API_KEY", "2c764d94714b43d4a2a0423f99085e8a");
            env.put("AGENT_SESSION_ID", "6cd40a7dcfb34b7bbc4adbe782f660b7");

            pb.redirectErrorStream(true); 
            Process process = pb.start();

            // 4. 读取脚本输出 (UTF-8 解决中文乱码)
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            // 5. 超时控制：等待 45 秒（考虑 Reasoner 模型深度思考比较慢）
            boolean finished = process.waitFor(45, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return Map.of("code", 504, "message", "AI 思考超时，请稍后重试");
            }

            // 获取 Python 打印的纯 JSON 字符串
            String pythonOutput = reader.lines().collect(Collectors.joining("\n")).trim();
            log.info("AI 脚本返回原始数据: {}", pythonOutput);

            // --- 核心修正点：将字符串解析为对象 ---
            // 这样 4 号拿到的 data 字段就是一个真正的 JSON 对象，而不是一串文字
            Object jsonData = objectMapper.readValue(pythonOutput, Object.class);

            return Map.of(
                "code", 200,
                "message", "success",
                "data", jsonData
            );

        } catch (Exception e) {
            log.error("后端处理 AI 请求失败", e);
            return Map.of("code", 500, "message", "后端解析异常: " + e.getMessage());
        }
    }
}