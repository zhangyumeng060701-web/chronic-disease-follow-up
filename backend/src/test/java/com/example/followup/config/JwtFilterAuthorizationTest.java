package com.example.followup.config;

import com.example.followup.security.CurrentUser;
import com.example.followup.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtFilterAuthorizationTest {
    private JwtUtil jwtUtil;
    private SecurityConfig.JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "integration-test-jwt-secret-with-at-least-32-bytes");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60000L);
        filter = new SecurityConfig.JwtAuthFilter(jwtUtil);
    }

    @Test
    void validJwtBuildsCurrentUserAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwtUtil.generateToken("doctorA", "DOCTOR", 7L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        CurrentUser principal = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(7L, principal.getUserId());
        assertEquals("DOCTOR", principal.getRole());
    }

    @Test
    void forgedJwtIsRejectedWithoutAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer forged.invalid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
