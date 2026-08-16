package com.example.followup.security;

import com.example.followup.constant.DomainConstants;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {
    private Long userId;
    private String username;
    private String role;

    public boolean isAdmin() {
        return DomainConstants.ROLE_ADMIN.equals(role);
    }
}
