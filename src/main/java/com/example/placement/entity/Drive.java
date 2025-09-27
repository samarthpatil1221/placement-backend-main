package com.example.placement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    @Lob
    private String jd; // job description

    private LocalDate dateOfDrive;
    private String location;

    private Long createdBy;
}
