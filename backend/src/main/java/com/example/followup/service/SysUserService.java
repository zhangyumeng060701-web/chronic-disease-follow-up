package com.example.followup.service;

import com.example.followup.dto.request.CreateUserRequest;
import com.example.followup.dto.request.UpdateUserRequest;
import com.example.followup.dto.request.UserQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.UserVO;

public interface SysUserService {
    PageResponse<UserVO> listUsers(UserQuery query);
    void createUser(CreateUserRequest request);
    void updateUser(Long id, UpdateUserRequest request);
    void toggleUserStatus(Long id);
}
