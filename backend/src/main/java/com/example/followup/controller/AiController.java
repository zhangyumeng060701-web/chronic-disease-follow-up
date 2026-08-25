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
 * 3号 AI 需求拆解核心 Controller
 * 已集成：环境变量动态注入、Jackson 对象化解析、超时控制、跨域支持
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin // 允许 4 号前端跨域联调
public class AiController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/decompose")
    public Map<String, Object> decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        
        // 1. 参数校验
        if (requirement == null || requirement.trim().isEmpty()) {
            return Map.of("code", 400, "message", "requirement不能为空");
        }

        try {
            log.info("开始调用 AI 拆解任务, 需求内容: {}", requirement);
            
            // 2. 脚本路径（基于 backend 运行目录）
            String scriptPath = "../ai-agent/scripts/decompose.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, requirement);
            
            // 3. 【核心安全改进】注入密钥作为环境变量
            // 这样 Python 脚本只需 os.getenv("AGENT_API_KEY") 即可读到，无需硬编码
            Map<String, String> env = pb.environment();
            env.put("AGENT_API_KEY", "2c764d94714b43d4a2a0423f99085e8a");
            env.put("AGENT_SESSION_ID", "6cd40a7dcfb34b7bbc4adbe782f660b7");

            pb.redirectErrorStream(true); // 合并错误流
            Process process = pb.start();

            // 4. 读取脚本输出结果
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            // 5. 超时强力控制：等待 20 秒（考虑盘古大模型 Reasoner 慢思考特性）
            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("AI 响应超时，已强制终止进程");
                return Map.of("code", 504, "message", "AI 响应超时");
            }

            // 获取 Python 吐出的 JSON 字符串
            String output = reader.lines().collect(Collectors.joining("\n")).trim();
            
            // 6. 逻辑收口：验证退出码与输出格式
            if (process.exitValue() == 0 && output.startsWith("{")) {
                // 将字符串解析为真正的 JSON 对象后返回
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
     * 辅助方法：使用 Jackson 将 Python 吐出的字符串解析为真正的 JSON 对象
     * 解决 4 号反馈的“前端无法直接按 data.tasks 读取”的问题
     */
    private Object jsonToMap(String json) {
        try {
            // 解析为 Object.class，Jackson 会根据内容自动转为 Map 或 List
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.error("JSON 对象化解析失败，回退为原始字符串: {}", e.getMessage());
            return json; 
        }
    }
}