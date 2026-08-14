package com.example.followup.service;

import com.example.followup.entity.OperationLog;
import com.example.followup.mapper.OperationLogMapper;
import com.example.followup.service.impl.OperationLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OperationLogServiceTest {
    @Mock OperationLogMapper mapper;
    @InjectMocks OperationLogServiceImpl service;

    @Test void logPersistsMetadataWithoutRequestPayload() {
        service.log("doctor", "UPDATE", "PATIENT", 7L);
        ArgumentCaptor<OperationLog> captor = ArgumentCaptor.forClass(OperationLog.class);
        verify(mapper).insert(captor.capture());
        OperationLog log = captor.getValue();
        assertEquals("doctor", log.getUsername());
        assertEquals("PATIENT", log.getTargetType());
        assertEquals(7L, log.getTargetId());
        assertEquals("127.0.0.1", log.getIpAddress());
    }
}
