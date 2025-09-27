package com.example.placement.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveDTO {
    private Long id;
    private String companyName;
    private String jd;
    private LocalDate dateOfDrive;
    private String location;
}
