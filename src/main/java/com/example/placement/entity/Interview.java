package com.example.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming each interview is linked to a student
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    // Date and time for interview
    private LocalDate interviewDate;

    // Interview status: scheduled, completed, etc.
    private String status;

    // Constructors, getters, setters

    public Interview() {}

    public Interview(Student student, LocalDate interviewDate, String status) {
        this.student = student;
        this.interviewDate = interviewDate;
        this.status = status;
    }

    // Getters and setters omitted for brevity
}