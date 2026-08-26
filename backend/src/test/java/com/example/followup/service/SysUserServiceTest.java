package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.UserVO;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.service.impl.SysUserServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), SysUser.class);
    }

    @Test
    @DisplayName("创建用户时密码经过 BCrypt 加密")
    void createUserEncodesPassword() {
        when(sysUserMapper.findByUsername("doctor")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encrypted");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("doctor");
        request.setPassword("123456");
        request.setRealName("李医生");
        request.setRole("DOCTOR");

        sysUserService.createUser(request);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(sysUserMapper).insert(captor.capture());
        assertEquals("$2a$10$encrypted", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("编辑用户时密码留空则保留原密码")
    void updateUserWithBlankPasswordKeepsOriginal() {
        SysUser existing = new SysUser();
        existing.setId(1L);
        existing.setUsername("doctor");
        existing.setPassword("old-encrypted");
        when(sysUserMapper.selectById(1L)).thenReturn(existing);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setRealName("李医生");
        request.setRole("DOCTOR");
        request.setPassword("");

        sysUserService.updateUser(1L, request);

        verify(passwordEncoder, never()).encode(any());
        assertEquals("old-encrypted", existing.getPassword());
    }

    @Test
    @DisplayName("用户列表返回 VO，不包含密码")
    void listUsersReturnsVoWithoutPassword() {
        Page<SysUser> page = new Page<>(1, 20);
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("secret");
        user.setRole("ADMIN");
        doAnswer(invocation -> {
            Page<SysUser> p = invocation.getArgument(0);
            p.setRecords(List.of(user));
            p.setTotal(1);
            return p;
        }).when(sysUserMapper).selectPage(any(Page.class), any());

        UserVO vo = sysUserService.listUsers(new UserQuery()).getRecords().get(0);

        assertEquals("admin", vo.getUsername());
    }
}
