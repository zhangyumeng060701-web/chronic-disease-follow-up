package com.example.followup.controller;

import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @PostMapping("/decompose")
    public String decompose(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        if (requirement == null || requirement.trim().isEmpty()) {
            return "{\"code\": 400, \"message\": \"需求文本不能为空\"}";
        }

        try {
            // 基于 backend 运行目录，定位脚本路径
            String scriptPath = "../ai-agent/scripts/decompose.py";
            
            // 构造进程
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, requirement);
            pb.redirectErrorStream(true); // 合并错误流到标准输出
            Process process = pb.start();

            // 读取输出（UTF-8）
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            // 增加超时控制：最多等待 15 秒（大模型响应较慢）
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                return "{\"code\": 504, \"message\": \"AI 响应超时\"}";
            }

            String result = reader.lines().collect(Collectors.joining("\n"));
            
            if (process.exitValue() != 0) {
                return "{\"code\": 500, \"message\": \"脚本执行失败\", \"detail\": \"" + result.replace("\"", "'") + "\"}";
            }

            return result;

        } catch (Exception e) {
            return "{\"code\": 500, \"message\": \"后端调用异常: " + e.getMessage() + "\"}";
        }
    }
}