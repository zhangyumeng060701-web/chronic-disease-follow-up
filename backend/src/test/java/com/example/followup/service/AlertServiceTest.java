package com.example.followup.service;

import com.example.followup.entity.Alert;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.AlertServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @Mock AlertMapper alertMapper;
    @Mock PatientMapper patientMapper;
    @InjectMocks AlertServiceImpl service;

    @Test void resolveMarksAlertAndSetsTime() {
        Alert alert = new Alert(); alert.setId(1L); alert.setIsResolved(0);
        when(alertMapper.selectById(1L)).thenReturn(alert);
        service.resolveAlert(1L);
        assertEquals(1, alert.getIsResolved());
        assertNotNull(alert.getResolveTime());
        verify(alertMapper).updateById(alert);
    }

    @Test void resolvingMissingAlertReturns404() {
        when(alertMapper.selectById(9L)).thenReturn(null);
        assertEquals(404, assertThrows(BusinessException.class, () -> service.resolveAlert(9L)).getCode());
    }
}
