package com.example.placement.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String studentName;   // Change from Long studentId to String studentName
    private String status;
    private String companyName;   // add company name here
    private OffsetDateTime appliedAt;
}
