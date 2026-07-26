package com.example.followup.service;

import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.SysUser;

public interface SysUserService {
    PageResponse<SysUser> listUsers(UserQuery query);
    void createUser(CreateUserRequest request);
    void updateUser(SysUser user);
    void toggleUserStatus(Long id);
}
