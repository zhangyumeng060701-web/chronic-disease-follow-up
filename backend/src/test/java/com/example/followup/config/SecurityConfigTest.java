package com.example.followup.config;
import com.example.followup.security.CurrentUser;
import com.example.followup.security.JwtAuthFilter;
import com.example.followup.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {
 @AfterEach void clear(){ SecurityContextHolder.clearContext(); }
 @Test void validTokenCreatesAuthentication() throws Exception {
  JwtUtil jwt=mock(JwtUtil.class); Claims c=mock(Claims.class);
  when(jwt.validateToken("valid")).thenReturn(true); when(jwt.parseToken("valid")).thenReturn(c);
  when(c.getSubject()).thenReturn("doctor"); when(c.get("role",String.class)).thenReturn("DOCTOR"); when(c.get("userId",Number.class)).thenReturn(7L);
  MockHttpServletRequest req=new MockHttpServletRequest(); req.addHeader("Authorization","Bearer valid");
  new JwtAuthFilter(jwt).doFilter(req,new MockHttpServletResponse(),new MockFilterChain());
  Authentication a=SecurityContextHolder.getContext().getAuthentication();
  assertAll(()->assertNotNull(a),()->assertEquals(7L,((CurrentUser)a.getPrincipal()).getUserId()),()->assertTrue(a.getAuthorities().stream().anyMatch(x->"ROLE_DOCTOR".equals(x.getAuthority()))));
 }
 @Test void invalidTokenReturns401() throws Exception { assertUnauthorized("bad",false,"DOCTOR",7L); }
 @Test void missingUserIdReturns401() throws Exception { assertUnauthorized("missing",true,"DOCTOR",null); }
 @Test void unknownRoleReturns401() throws Exception { assertUnauthorized("unknown",true,"GUEST",7L); }
 private void assertUnauthorized(String token,boolean valid,String role,Long id) throws Exception {
  JwtUtil jwt=mock(JwtUtil.class); Claims c=mock(Claims.class); when(jwt.validateToken(token)).thenReturn(valid);
  if(valid){when(jwt.parseToken(token)).thenReturn(c);when(c.getSubject()).thenReturn("user");when(c.get("role",String.class)).thenReturn(role);when(c.get("userId",Number.class)).thenReturn(id);}
  MockHttpServletRequest req=new MockHttpServletRequest();req.addHeader("Authorization","Bearer "+token);MockHttpServletResponse res=new MockHttpServletResponse();
  new JwtAuthFilter(jwt).doFilter(req,res,new MockFilterChain());assertEquals(401,res.getStatus());assertNull(SecurityContextHolder.getContext().getAuthentication());
 }
}
