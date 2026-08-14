package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UserQuery;
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
 @Test void emptyPasswordUpdatePreservesHash(){SysUser old=user(1L,"$2a$old",1);when(mapper.selectById(1L)).thenReturn(old);SysUser update=user(1L,"",1);service.updateUser(update);assertEquals("$2a$old",update.getPassword());verify(encoder,never()).encode(anyString());}
 @Test void nonEmptyPasswordUpdateHashesNewValue(){when(mapper.selectById(1L)).thenReturn(user(1L,"old",1));when(encoder.encode("new")).thenReturn("$2a$new");SysUser update=user(1L,"new",1);service.updateUser(update);assertEquals("$2a$new",update.getPassword());}
 @Test void missingUserUpdateReturns404(){when(mapper.selectById(9L)).thenReturn(null);assertEquals(404,assertThrows(BusinessException.class,()->service.updateUser(user(9L,"",1))).getCode());}
 @Test void toggleStatusPersistsChange(){SysUser u=user(1L,"hash",1);when(mapper.selectById(1L)).thenReturn(u);service.toggleUserStatus(1L);assertEquals(0,u.getStatus());verify(mapper).updateById(u);}
 @Test void listDoesNotExposePassword(){doAnswer(i->{Page<SysUser> p=i.getArgument(0);p.setRecords(List.of(user(1L,"secret",1)));p.setTotal(1);return p;}).when(mapper).selectPage(any(Page.class),any());assertNull(service.listUsers(new UserQuery()).getRecords().get(0).getPassword());}
 private CreateUserRequest create(){CreateUserRequest r=new CreateUserRequest();r.setUsername("doctor");r.setPassword("plain");r.setRealName("医生");r.setRole("DOCTOR");return r;}
 private SysUser user(Long id,String password,int status){SysUser u=new SysUser();u.setId(id);u.setUsername("doctor");u.setPassword(password);u.setStatus(status);return u;}
}
