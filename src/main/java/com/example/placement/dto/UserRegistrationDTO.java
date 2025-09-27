package com.example.placement.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String role; // "STUDENT" or "ADMIN"
}
