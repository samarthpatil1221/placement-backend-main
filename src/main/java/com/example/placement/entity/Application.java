package com.example.placement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // Add eager fetch here
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)  // Also eager for drive to avoid lazy issues
    @JoinColumn(name = "drive_id")
    private Drive drive;

    private String status; // APPLIED, SHORTLISTED, SELECTED, REJECTED

    private OffsetDateTime appliedAt;
 // com/example/placement/entity/Application.java
    public void setAppliedAt(OffsetDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

}

