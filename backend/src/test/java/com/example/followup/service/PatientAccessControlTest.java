package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.*;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientAccessControlTest {
 @Mock PatientMapper mapper; @Spy PatientMaskingService masking=new PatientMaskingService(); @InjectMocks PatientServiceImpl service;
 @BeforeAll static void initTableMetadata(){TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(),"test"),Patient.class);}
 @AfterEach void clear(){SecurityContextHolder.clearContext();}
 @Test void doctorListReturnsMaskedPatients(){doctor(7L);doAnswer(i->{Page<Patient> p=i.getArgument(0);p.setRecords(List.of(patient(1L,7L)));p.setTotal(1);return p;}).when(mapper).selectPage(any(Page.class),any());assertEquals("张*",service.listPatients(new PatientQuery()).getRecords().get(0).getName());ArgumentCaptor<com.baomidou.mybatisplus.core.conditions.Wrapper<Patient>> c=ArgumentCaptor.forClass(com.baomidou.mybatisplus.core.conditions.Wrapper.class);verify(mapper).selectPage(any(Page.class),c.capture());assertTrue(c.getValue().getSqlSegment().contains("doctor_id"));}
 @Test void doctorCanReadOwnPatient(){doctor(7L);when(mapper.selectById(1L)).thenReturn(patient(1L,7L));assertEquals(1L,service.getPatientById(1L).getId());}
 @Test void doctorCannotReadOtherDoctorsPatient(){doctor(7L);when(mapper.selectById(1L)).thenReturn(patient(1L,8L));assertEquals(404,assertThrows(BusinessException.class,()->service.getPatientById(1L)).getCode());}
 @Test void doctorCreationForcesOwnId(){doctor(7L);Patient p=patient(null,99L);service.addPatient(p);assertEquals(7L,p.getDoctorId());verify(mapper).insert(p);}
 @Test void doctorUpdateCannotTransferPatient(){doctor(7L);when(mapper.selectById(1L)).thenReturn(patient(1L,7L));Patient p=patient(1L,99L);service.updatePatient(p);assertEquals(7L,p.getDoctorId());}
 @Test void doctorCannotDeleteOtherDoctorsPatient(){doctor(7L);when(mapper.selectById(1L)).thenReturn(patient(1L,8L));assertThrows(BusinessException.class,()->service.deletePatient(1L));verify(mapper,never()).updateById(any());}
 @Test void adminReceivesOriginalPatient(){admin();when(mapper.selectById(1L)).thenReturn(patient(1L,8L));assertEquals("张三",service.getPatientById(1L).getName());}
 private void doctor(Long id){auth(new CurrentUser(id,"doctor","DOCTOR"),"ROLE_DOCTOR");} private void admin(){auth(new CurrentUser(1L,"admin","ADMIN"),"ROLE_ADMIN");}
 private void auth(CurrentUser u,String role){SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u,null,List.of(new SimpleGrantedAuthority(role))));}
 private Patient patient(Long id,Long doctor){Patient p=new Patient();p.setId(id);p.setName("张三");p.setPhone("13812345678");p.setIdCard("320102199001011234");p.setAddress("南京市鼓楼区汉口路");p.setDoctorId(doctor);p.setStatus(1);return p;}
}
