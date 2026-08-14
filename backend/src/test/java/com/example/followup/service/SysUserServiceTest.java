package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {
 @Mock SysUserMapper mapper; @Mock PasswordEncoder encoder; @InjectMocks SysUserServiceImpl service;
 @Test void createHashesPassword(){CreateUserRequest r=create();when(encoder.encode("plain")).thenReturn("$2a$hash");service.createUser(r);ArgumentCaptor<SysUser> c=ArgumentCaptor.forClass(SysUser.class);verify(mapper).insert(c.capture());assertEquals("$2a$hash",c.getValue().getPassword());}
 @Test void duplicateUsernameIsRejected(){CreateUserRequest r=create();when(mapper.findByUsername("doctor")).thenReturn(user(1L,"hash",1));assertEquals(400,assertThrows(BusinessException.class,()->service.createUser(r)).getCode());}
 @Test void emptyPasswordUpdatePreservesHash(){SysUser old=user(1L,"$2a$old",1);when(mapper.selectById(1L)).thenReturn(old);service.updateUser(1L,update(""));assertEquals("$2a$old",old.getPassword());verify(encoder,never()).encode(anyString());}
 @Test void nonEmptyPasswordUpdateHashesNewValue(){SysUser old=user(1L,"old",1);when(mapper.selectById(1L)).thenReturn(old);when(encoder.encode("new")).thenReturn("$2a$new");service.updateUser(1L,update("new"));assertEquals("$2a$new",old.getPassword());}
 @Test void updateDoesNotOverwriteIdentityOrStatus(){SysUser old=user(1L,"hash",0);old.setUsername("fixed");when(mapper.selectById(1L)).thenReturn(old);UpdateUserRequest request=update("");request.setRealName("New Name");service.updateUser(1L,request);assertEquals("fixed",old.getUsername());assertEquals(0,old.getStatus());assertEquals("New Name",old.getRealName());}
 @Test void missingUserUpdateReturns404(){when(mapper.selectById(9L)).thenReturn(null);assertEquals(404,assertThrows(BusinessException.class,()->service.updateUser(9L,update(""))).getCode());}
 @Test void toggleStatusPersistsChange(){SysUser u=user(1L,"hash",1);when(mapper.selectById(1L)).thenReturn(u);service.toggleUserStatus(1L);assertEquals(0,u.getStatus());verify(mapper).updateById(u);}
 @Test void listUsesPasswordFreeViewModel(){doAnswer(i->{Page<SysUser> p=i.getArgument(0);p.setRecords(List.of(user(1L,"secret",1)));p.setTotal(1);return p;}).when(mapper).selectPage(any(Page.class),any());assertEquals("doctor",service.listUsers(new UserQuery()).getRecords().get(0).getUsername());}
 private CreateUserRequest create(){CreateUserRequest r=new CreateUserRequest();r.setUsername("doctor");r.setPassword("plain");r.setRealName("医生");r.setRole("DOCTOR");return r;}
 private SysUser user(Long id,String password,int status){SysUser u=new SysUser();u.setId(id);u.setUsername("doctor");u.setPassword(password);u.setStatus(status);return u;}
 private UpdateUserRequest update(String password){UpdateUserRequest r=new UpdateUserRequest();r.setPassword(password);r.setRealName("Doctor");r.setRole("DOCTOR");r.setPhone("13800000000");return r;}
}
