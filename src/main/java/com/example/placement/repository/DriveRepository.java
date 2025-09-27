package com.example.placement.repository;

import com.example.placement.entity.Drive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriveRepository extends JpaRepository<Drive, Long> {
	@Query("SELECT COUNT(d) FROM Drive d")
	  int countAllDrives();
}
