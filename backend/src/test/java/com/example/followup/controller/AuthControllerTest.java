package com.example.followup.controller;

import com.example.followup.entity.SysUser;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {
    private final JwtUtil jwt = mock(JwtUtil.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final SysUserMapper mapper = mock(SysUserMapper.class);
    private MockMvc mvc;

    @BeforeEach void setUp() {
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "jwtUtil", jwt);
        ReflectionTestUtils.setField(controller, "passwordEncoder", encoder);
        ReflectionTestUtils.setField(controller, "sysUserMapper", mapper);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test void loginReturnsToken() throws Exception {
        SysUser user=user(1,"123456"); when(mapper.findByUsername("admin")).thenReturn(user);
        when(encoder.matches("123456","123456")).thenReturn(true);
        when(jwt.generateToken(1L,"admin","ADMIN")).thenReturn("token");
        mvc.perform(login("123456")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token"));
    }
    @Test void unknownUserIsRejected() throws Exception {
        when(mapper.findByUsername("admin")).thenReturn(null);
        mvc.perform(login("123456")).andExpect(jsonPath("$.code").value(401));
    }
    @Test void wrongPasswordIsRejected() throws Exception {
        when(mapper.findByUsername("admin")).thenReturn(user(1,"secret"));
        when(encoder.matches("wrong","secret")).thenReturn(false);
        mvc.perform(login("wrong")).andExpect(jsonPath("$.code").value(401));
    }
    @Test void disabledUserIsRejected() throws Exception {
        when(mapper.findByUsername("admin")).thenReturn(user(0,"123456"));
        mvc.perform(login("123456")).andExpect(jsonPath("$.code").value(403));
    }
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder login(String password) {
        return post("/api/auth/login").contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\""+password+"\"}");
    }
    private SysUser user(int status,String password) { SysUser u=new SysUser(); u.setId(1L); u.setUsername("admin"); u.setRole("ADMIN"); u.setRealName("管理员"); u.setStatus(status); u.setPassword(password); return u; }
}
