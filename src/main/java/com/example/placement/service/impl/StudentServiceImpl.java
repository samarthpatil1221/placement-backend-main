package com.example.placement.service.impl;

import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.repository.StudentRepository;
import com.example.placement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired private StudentRepository studentRepository;

    @Override
    public Student createForUser(User user) {
        Student s = new Student();
        s.setUser(user);
        s.setEmail(null);
        return studentRepository.save(s);
    }

    @Override
    public Student findByUser(User user) {
        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @Override
    public Student update(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void updateResumePath(Long studentId, String path) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) throw new RuntimeException("Student not found");
        Student s = opt.get();
        s.setResumePath(path);
        studentRepository.save(s);
    }

    @Override
    public void delete(Student student) {
        studentRepository.delete(student);
    }
}
