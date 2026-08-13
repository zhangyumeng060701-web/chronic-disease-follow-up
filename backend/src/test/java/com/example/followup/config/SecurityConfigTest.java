package com.example.followup.config;

import com.example.followup.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {
    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void validTokenAddsRequestAttributes() throws Exception {
        JwtUtil jwt=mock(JwtUtil.class); Claims claims=mock(Claims.class);
        when(jwt.validateToken("valid")).thenReturn(true); when(jwt.parseToken("valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("doctor"); when(claims.get("role")).thenReturn("DOCTOR");
        MockHttpServletRequest request=new MockHttpServletRequest(); request.addHeader("Authorization","Bearer valid");
        new SecurityConfig.JwtAuthFilter(jwt).doFilter(request,new MockHttpServletResponse(),new MockFilterChain());
        assertEquals("doctor",request.getAttribute("username")); assertEquals("DOCTOR",request.getAttribute("role"));
    }
    @Test void invalidTokenDoesNotAuthenticate() throws Exception {
        JwtUtil jwt=mock(JwtUtil.class); when(jwt.validateToken("bad")).thenReturn(false);
        MockHttpServletRequest request=new MockHttpServletRequest(); request.addHeader("Authorization","Bearer bad");
        new SecurityConfig.JwtAuthFilter(jwt).doFilter(request,new MockHttpServletResponse(),new MockFilterChain());
        assertNull(SecurityContextHolder.getContext().getAuthentication()); assertNull(request.getAttribute("role"));
    }
}
