package com.example.followup.dto.request;

import lombok.Data;

@Data
public class PatientQuery {
    private Integer page = 1;
    private Integer size = 20;
    private String name;
    private String diseaseType;
}