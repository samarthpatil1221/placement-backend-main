package com.example.placement.controller;

import com.example.placement.dto.ApplicationDTO;
import com.example.placement.entity.Application;
import com.example.placement.entity.Drive;
import com.example.placement.entity.User;
import com.example.placement.service.AdminService;
import com.example.placement.repository.ApplicationRepository;
import com.example.placement.repository.UserRepository;
import com.example.placement.service.DriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DriveService driveService;
    @Autowired private AdminService adminService;

    @GetMapping("/applications")
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    @GetMapping("/drives/{driveId}/applications")
    public List<ApplicationDTO> getApplicationsByDrive(@PathVariable Long driveId) {
        List<Application> applications = applicationRepository.findByDriveId(driveId);
        return applications.stream().map(app -> new ApplicationDTO(
                app.getId(),
                app.getStudent() != null ? app.getStudent().getFullName() : "Unknown",
                app.getStatus(),
                app.getDrive() != null ? app.getDrive().getCompanyName() : "Unknown",
                app.getAppliedAt()
        )).collect(Collectors.toList());

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/company-application-stats")
    public ResponseEntity<List<Map<String, Object>>> getCompanyApplicationStats() {
        List<Drive> drives = driveService.listAll();
        List<User> allStudents = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT")))
                .collect(Collectors.toList());
        int totalStudents = allStudents.size();

        List<Map<String, Object>> stats = drives.stream().map(drive -> {
            int appliedCount = applicationRepository.findByDriveId(drive.getId()).size();
            int notAppliedCount = totalStudents - appliedCount;

            Map<String, Object> map = new HashMap<>();
            map.put("companyName", drive.getCompanyName());
            map.put("appliedCount", appliedCount);
            map.put("notAppliedCount", notAppliedCount);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(stats);
    }
    
    
    @PutMapping("/applications/{id}/status")
    public ResponseEntity<?> adminUpdateApplicationStatus(@PathVariable Long id,
                                                          @RequestBody Map<String,String> body) {
        String newStatus = body.getOrDefault("status","").toUpperCase();
        if (!List.of("APPLIED","PENDING","SHORTLISTED","INTERVIEW","SELECTED","REJECTED").contains(newStatus)) {
            return ResponseEntity.badRequest().body(Map.of("error","Invalid status"));
        }
        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(newStatus);
        applicationRepository.save(app);
        return ResponseEntity.ok(Map.of("status","updated"));
    }

}
