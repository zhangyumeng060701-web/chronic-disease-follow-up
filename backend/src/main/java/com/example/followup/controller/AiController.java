package com.example.followup.controller;

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

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        
        try {
            log.info("开始调用 AI 拆解任务, 需求内容: {}", requirement);
            
            String scriptPath = "../ai-agent/scripts/decompose.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, requirement);
            
            // 解决安全：可以在这里向 ProcessBuilder 注入环境变量
            // pb.environment().put("AGENT_API_KEY", "你的KEY"); 

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

            String output = reader.lines().collect(Collectors.joining("\n"));
            
            // 统一返回格式 Result<T> 的模拟实现
            if (process.exitValue() == 0 && output.startsWith("{")) {
                // 如果 Python 已经返回了 JSON 字符串，直接返回（Spring 会自动转为 JSON 对象）
                // 建议这里用 Jackson 解析一下确保格式合法
                return Map.of("code", 200, "message", "success", "data", jsonToMap(output));
            } else {
                log.error("AI 脚本执行异常: {}", output);
                return Map.of("code", 500, "message", "AI 拆解失败", "detail", output);
            }

        } catch (Exception e) {
            log.error("后端接口异常", e);
            return Map.of("code", 500, "message", "系统错误: " + e.getMessage());
        }
    }

    // 简易工具：将 Python 字符串转为 Map
    private Object jsonToMap(String json) {
        // 实际开发中请使用 ObjectMapper.readValue
        return json; 
    }
}