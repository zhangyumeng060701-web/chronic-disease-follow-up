package com.example.followup.config;

import com.example.followup.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("jwt filter should set authentication for valid token")
    void jwtFilter_shouldSetAuthenticationForValidToken() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.parseToken("valid-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("doctor");
        when(claims.get("role")).thenReturn("DOCTOR");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        new SecurityConfig.JwtAuthFilter(jwtUtil)
                .doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("doctor", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_DOCTOR".equals(authority.getAuthority())));
        assertEquals("DOCTOR", request.getAttribute("role"));
    }

    @Test
    @DisplayName("jwt filter should leave authentication empty for invalid token")
    void jwtFilter_shouldLeaveAuthenticationEmptyForInvalidToken() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad-token");

        new SecurityConfig.JwtAuthFilter(jwtUtil)
                .doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
