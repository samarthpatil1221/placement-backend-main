package com.example.placement.service;

import com.example.placement.entity.Student;
import com.example.placement.entity.User;

public interface StudentService {
    Student createForUser(User user);
    Student findByUser(User user);
    Student update(Student student);
    void updateResumePath(Long studentId, String path);
    void delete(Student student);    // Added delete method
}
