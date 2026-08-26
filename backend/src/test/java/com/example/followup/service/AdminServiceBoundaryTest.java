package com.example.followup.service;

import com.example.followup.dto.request.UserQuery;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceBoundaryTest {
    @Mock SysUserMapper sysUserMapper;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks SysUserServiceImpl service;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doctorCannotListUsers() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(7L, "doctorA", "DOCTOR"), null,
                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.listUsers(new UserQuery()));

        assertEquals(403, exception.getHttpStatus());
        verify(sysUserMapper, never()).selectPage(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }
}
