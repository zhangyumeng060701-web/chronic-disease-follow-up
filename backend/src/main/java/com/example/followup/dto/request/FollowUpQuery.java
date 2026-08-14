package com.example.followup.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FollowUpQuery {
    private Integer page = 1;
    private Integer size = 20;
    private Long patientId;
    private LocalDate startDate;
    private LocalDate endDate;
}
