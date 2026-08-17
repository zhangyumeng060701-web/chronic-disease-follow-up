package com.example.followup.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "chronic-disease-follow-up-secret-key-2026-test-abcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
    }

    @Test
    @DisplayName("生成的 token 可以解析并包含用户信息")
    void generatedTokenCanBeParsed() {
        String token = jwtUtil.generateToken("doctor", "DOCTOR", 7L);

        assertTrue(jwtUtil.validateToken(token));
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("doctor", claims.getSubject());
        assertEquals("DOCTOR", claims.get("role"));
        assertEquals(7L, ((Number) claims.get("userId")).longValue());
    }

    @Test
    @DisplayName("过期 token 校验失败")
    void expiredTokenIsInvalid() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String token = jwtUtil.generateToken("doctor", "DOCTOR", 7L);

        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("非法 token 校验失败")
    void invalidTokenIsRejected() {
        assertFalse(jwtUtil.validateToken("not-a-valid-token"));
        assertNotNull(jwtUtil.parseToken(jwtUtil.generateToken("admin", "ADMIN", 1L)));
    }
}
