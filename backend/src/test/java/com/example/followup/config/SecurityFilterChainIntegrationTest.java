package com.example.followup.config;

import com.example.followup.controller.PatientController;
import com.example.followup.controller.UserController;
import com.example.followup.service.PatientService;
import com.example.followup.service.SysUserService;
import com.example.followup.util.JwtUtil;
import com.example.followup.exception.BusinessException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers={PatientController.class,UserController.class})
@Import(SecurityConfig.class)
class SecurityFilterChainIntegrationTest {
 @Autowired MockMvc mvc; @MockBean JwtUtil jwt; @MockBean PatientService patients; @MockBean SysUserService users;
 @BeforeEach void tokens(){token("admin",1L,"ADMIN");token("doctor",7L,"DOCTOR");}
 @Test void unauthenticatedPatientRequestReturns401() throws Exception {mvc.perform(get("/api/patients")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));}
 @Test void invalidTokenReturns401() throws Exception {when(jwt.validateToken("bad")).thenReturn(false);mvc.perform(get("/api/patients").header("Authorization","Bearer bad")).andExpect(status().isUnauthorized());}
 @Test void doctorCanAccessPatientApi() throws Exception {mvc.perform(get("/api/patients").header("Authorization","Bearer doctor")).andExpect(status().isOk());}
 @Test void doctorCannotAccessUserManagement() throws Exception {mvc.perform(get("/api/users").header("Authorization","Bearer doctor")).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403));}
 @Test void adminCanAccessUserManagement() throws Exception {mvc.perform(get("/api/users").header("Authorization","Bearer admin")).andExpect(status().isOk());}
 @Test void authenticatedBusiness404Remains404() throws Exception {when(patients.getPatientById(99L)).thenThrow(new BusinessException(404,"患者不存在"));mvc.perform(get("/api/patients/99").header("Authorization","Bearer doctor")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));}
 private void token(String value,Long id,String role){Claims c=mock(Claims.class);when(jwt.validateToken(value)).thenReturn(true);when(jwt.parseToken(value)).thenReturn(c);when(c.getSubject()).thenReturn(value);when(c.get("userId",Number.class)).thenReturn(id);when(c.get("role",String.class)).thenReturn(role);}
}
