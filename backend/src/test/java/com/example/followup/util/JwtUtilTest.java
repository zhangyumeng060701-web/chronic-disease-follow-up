package com.example.followup.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
 @Test void generatedTokenContainsIdentityAndRole(){JwtUtil jwt=jwt(60000L);String token=jwt.generateToken(7L,"doctor","DOCTOR");Claims c=jwt.parseToken(token);assertAll(()->assertEquals("doctor",c.getSubject()),()->assertEquals(7,c.get("userId")),()->assertEquals("DOCTOR",c.get("role")),()->assertTrue(jwt.validateToken(token)));}
 @Test void expiredTokenIsRejected(){JwtUtil jwt=jwt(-1000L);assertFalse(jwt.validateToken(jwt.generateToken(7L,"doctor","DOCTOR")));}
 private JwtUtil jwt(long expiration){JwtUtil jwt=new JwtUtil();ReflectionTestUtils.setField(jwt,"secret","unit-test-only-secret-with-at-least-32-bytes");ReflectionTestUtils.setField(jwt,"expiration",expiration);return jwt;}
}
