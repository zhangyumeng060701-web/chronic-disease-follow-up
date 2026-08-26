package com.example.followup.service; // 注意包名

import com.example.followup.controller.AiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiServiceTest {

    @Autowired
    private AiController aiController; // 注入你写的接口

    /**
     * 测试 1：验证正常拆解流程
     */
    @Test
    void testDecomposeSuccess() {
        Map<String, String> req = Map.of("requirement", "测试需求：增加一个导出按钮");
        Map<String, Object> result = aiController.decompose(req);
        
        assertNotNull(result);
        assertEquals(200, result.get("code"), "成功时状态码应为200");
        assertNotNull(result.get("data"), "返回数据不能为空");
    }

    /**
     * 测试 2：验证空需求处理
     */
    @Test
    void testDecomposeEmpty() {
        Map<String, String> req = Map.of("requirement", "");
        Map<String, Object> result = aiController.decompose(req);
        
        assertEquals(400, result.get("code"), "空需求应返回400");
    }
}