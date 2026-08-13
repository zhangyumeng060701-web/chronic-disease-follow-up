package com.example.followup.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {
    private Long userId;
    private String username;
    private String role;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
