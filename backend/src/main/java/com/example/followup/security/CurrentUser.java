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
    private Long patientId;

    public CurrentUser(Long userId, String username, String role) {
        this(userId, username, role, null);
    }

    public boolean isAdmin() {
        return DomainConstants.ROLE_ADMIN.equals(role);
    }

    public boolean isPatient() {
        return DomainConstants.ROLE_PATIENT.equals(role);
    }
}
