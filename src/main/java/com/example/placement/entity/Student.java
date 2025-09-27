package com.example.placement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", unique=true)
    private User user;

    private String fullName;
    private String email;
    private String phone;
    private String resumePath;
    private String batch;
    private String college;
}
