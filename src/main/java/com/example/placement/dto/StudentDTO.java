package com.example.placement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String resumePath;
    private String batch;
    private String college;
}
