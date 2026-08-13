package com.example.followup.dto.request;

import lombok.Data;

@Data
public class UserQuery {
    private Integer page = 1;
    private Integer size = 20;
    private String username;
    private String role;
}
