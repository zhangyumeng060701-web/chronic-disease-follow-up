package com.example.followup.controller;

import com.example.followup.entity.SysUser;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SysUserMapper sysUserMapper;
    @InjectMocks
    private AuthController authController;

    @Test
    void loginReturnsToken() throws Exception {
        when(sysUserMapper.findByUsername("admin")).thenReturn(adminUser());
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ADMIN", 1L)).thenReturn("token");

        mockMvc().perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("token"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        when(sysUserMapper.findByUsername("admin")).thenReturn(adminUser());
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        mockMvc().perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void disabledUserReturns403() throws Exception {
        SysUser user = adminUser();
        user.setStatus(0);
        when(sysUserMapper.findByUsername("admin")).thenReturn(user);

        mockMvc().perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(authController).build();
    }

    private SysUser adminUser() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("$2a$10$abcdefghijklmnopqrstuv");
        user.setRealName("管理员");
        user.setRole("ADMIN");
        user.setStatus(1);
        return user;
    }
}
