package com.example.followup.controller;

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
