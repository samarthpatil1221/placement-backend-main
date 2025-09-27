package com.example.placement.service;

import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.repository.ApplicationRepository;
import com.example.placement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentService studentService;
    @Autowired private ApplicationRepository applicationRepository;

    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = null;
        try {
            student = studentService.findByUser(user);
        } catch (RuntimeException e) {
            // Student not found, proceed to delete user
        }

        if (student != null) {
            applicationRepository.deleteByStudentId(student.getId());
            applicationRepository.flush(); // flush to apply deletes immediately
            studentService.delete(student);
        }
        userRepository.delete(user);
    }
}
