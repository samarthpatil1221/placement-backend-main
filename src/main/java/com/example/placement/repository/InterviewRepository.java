package com.example.placement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.placement.entity.Interview;
import com.example.placement.entity.Student;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
  List<Interview> findByStudent(Student student);}