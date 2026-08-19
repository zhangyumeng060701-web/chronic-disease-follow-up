package com.example.followup.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // 1. 增加 Jackson 导入
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    // 2. 初始化 ObjectMapper（Spring Boot 自带，用于解析 JSON）
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        
        try {
            log.info("开始调用 AI 拆解任务, 需求内容: {}", requirement);
            
            String scriptPath = "../ai-agent/scripts/decompose.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, requirement);
            
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            // 15秒超时硬控制
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return Map.of("code", 504, "message", "AI 响应超时", "data", null);
            }

            // 获取 Python 打印的 JSON 字符串
            String output = reader.lines().collect(Collectors.joining("\n")).trim();
            
            if (process.exitValue() == 0 && output.startsWith("{")) {
                // 3. 核心修正：调用下方补全的解析方法
                return Map.of(
                    "code", 200, 
                    "message", "success", 
                    "data", jsonToMap(output) 
                );
            } else {
                log.error("AI 脚本执行异常: {}", output);
                return Map.of("code", 500, "message", "AI 拆解失败", "detail", output);
            }

        } catch (Exception e) {
            log.error("后端接口异常", e);
            return Map.of("code", 500, "message", "系统错误: " + e.getMessage());
        }
    }

    /**
     * 补全后的解析方法：将 Python 吐出的字符串解析为真正的 JSON 对象
     */
    private Object jsonToMap(String json) {
        try {
            // 将字符串解析为 Object（Jackson 会根据 JSON 内容自动转为 Map 或 List）
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.error("JSON 解析失败: {}", e.getMessage());
            return json; // 解析失败则降级返回字符串
        }
    }
}