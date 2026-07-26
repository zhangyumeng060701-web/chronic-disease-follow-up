package com.example.followup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStats {
    private Long doctorId;
    private String doctorName;
    private Long patientCount;
    private String completionRate;
    private Long highRiskCount;
}
