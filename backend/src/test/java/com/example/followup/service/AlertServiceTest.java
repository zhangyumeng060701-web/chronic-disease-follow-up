package com.example.followup.service;

import com.example.followup.entity.Alert;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.AlertServiceImpl;
import com.example.followup.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertMapper alertMapper;
    @Mock
    private PatientMapper patientMapper;
    @InjectMocks
    private AlertServiceImpl alertService;

    @BeforeEach
    void authenticateAdmin() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(1L, "admin", "ADMIN"), null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("不存在的预警返回 404")
    void resolveNotFound() {
        when(alertMapper.selectById(9L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> alertService.resolveAlert(9L));
        assertEquals(404, exception.getHttpStatus());
    }

    @Test
    @DisplayName("处理预警后更新为已解决")
    void resolveUpdatesAlert() {
        Alert alert = new Alert();
        alert.setId(9L);
        alert.setIsResolved(0);
        when(alertMapper.selectById(9L)).thenReturn(alert);

        alertService.resolveAlert(9L);

        assertEquals(1, alert.getIsResolved());
        verify(alertMapper).updateById(alert);
    }
}
