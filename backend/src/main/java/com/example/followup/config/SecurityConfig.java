/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.config;

import com.example.followup.security.CurrentUser;
import com.example.followup.util.JwtUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SecurityConfig 配置。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 执行filterChain操作。
     *
     * @param http 参数说明
     * @param jwtUtil 参数说明
     * @return 返回值
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http.csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/login", "/api/patient/login", "/api/health",
                    "/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    static class JwtAuthFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;
        JwtAuthFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

        /**
         * 执行doFilterInternal操作。
         *
         * @param request 参数说明
         * @param response 参数说明
         * @param chain 参数说明
         */
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtil.validateToken(token)) {
                    var claims = jwtUtil.parseToken(token);
                    String username = claims.getSubject();
                    Object roleValue = claims.get("role");
                    String role = roleValue == null ? "" : String.valueOf(roleValue);
                    Object userIdValue = claims.get("userId");
                    Long userId = userIdValue == null ? null : Long.valueOf(String.valueOf(userIdValue));
                    Object patientIdValue = claims.get("patientId");
                    Long patientId = patientIdValue == null ? null : Long.valueOf(String.valueOf(patientIdValue));
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                    CurrentUser currentUser = new CurrentUser(userId, username, role, patientId);
                    var authentication = new UsernamePasswordAuthenticationToken(currentUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    request.setAttribute("username", username);
                    request.setAttribute("role", role);
                    request.setAttribute("userId", userId);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            chain.doFilter(request, response);
        }
    }
}
