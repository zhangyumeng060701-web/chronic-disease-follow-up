/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.UserVO;
import com.example.followup.service.SysUserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private SysUserService sysUserService;
    @InjectMocks
    private UserController userController;

    @Test
    void listReturnsUsers() throws Exception {
        PageResponse<UserVO> page = new PageResponse<>();
        UserVO vo = new UserVO();
        vo.setId(1L);
        vo.setUsername("admin");
        page.setRecords(List.of(vo));
        page.setTotal(1);
        page.setPage(1);
        page.setSize(20);
        when(sysUserService.listUsers(any(UserQuery.class))).thenReturn(page);

        mockMvc().perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].username").value("admin"));
    }

    @Test
    void createCallsService() throws Exception {
        mockMvc().perform(post("/api/users")
                        .contentType("application/json")
                        .content("{\"username\":\"doctor\",\"password\":\"123456\",\"realName\":\"李医生\",\"role\":\"DOCTOR\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateUserRequest> captor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(sysUserService).createUser(captor.capture());
        assertEquals("doctor", captor.getValue().getUsername());
    }

    @Test
    void updateCallsService() throws Exception {
        mockMvc().perform(put("/api/users/9")
                        .contentType("application/json")
                        .content("{\"realName\":\"李医生\",\"role\":\"DOCTOR\",\"phone\":\"13812345678\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdateUserRequest> captor = ArgumentCaptor.forClass(UpdateUserRequest.class);
        verify(sysUserService).updateUser(eq(9L), captor.capture());
        assertEquals("李医生", captor.getValue().getRealName());
    }

    @Test
    void toggleStatusCallsService() throws Exception {
        mockMvc().perform(put("/api/users/9/toggle-status"))
                .andExpect(status().isOk());
        verify(sysUserService).toggleUserStatus(9L);
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(userController).build();
    }
}
