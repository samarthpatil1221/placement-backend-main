package com.example.placement.repository;

import com.example.placement.entity.Application;
import com.example.placement.entity.Drive;
import com.example.placement.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(Student student);
    List<Application> findByDriveId(Long driveId);

    long countByStudentId(Long studentId);
    long countByStudentIdAndStatus(Long studentId, String status);
    @Modifying
    @Transactional
    @Query("DELETE FROM Application a WHERE a.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Long studentId);
    boolean existsByStudentAndDrive(Student student, Drive drive);
  String findByStudentAndDrive(Student student, Drive d);
  @Query("SELECT COUNT(a) FROM Application a WHERE a.student.id = :studentId")
  int countApplicationsByStudentId(@Param("studentId") Long studentId);
}