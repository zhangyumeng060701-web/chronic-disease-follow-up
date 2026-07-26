package com.example.followup.dto.request;

import lombok.Data;

@Data
public class AlertQuery {
    private Integer page = 1;
    private Integer size = 20;
    private String alertType;
    private String alertLevel;
    private Integer isResolved;
}
