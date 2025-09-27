package com.example.placement.controller;

import com.example.placement.dto.DriveDTO;
import com.example.placement.entity.Application;
import com.example.placement.entity.Drive;
import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.repository.ApplicationRepository;
import com.example.placement.service.DriveService;
import com.example.placement.service.StudentService;
import com.example.placement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drives")
public class DriveController {

    @Autowired private DriveService driveService;
    @Autowired private UserService userService;
    @Autowired private StudentService studentService;
    @Autowired private ApplicationRepository applicationRepository;

    @GetMapping
    public ResponseEntity<List<DriveDTO>> list() {
        List<DriveDTO> list = driveService.listAll().stream()
                .map(d -> new DriveDTO(d.getId(), d.getCompanyName(), d.getJd(), d.getDateOfDrive(), d.getLocation()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<DriveDTO> createDrive(@RequestBody DriveDTO dto, Principal principal) {
        Drive d = new Drive();
        d.setCompanyName(dto.getCompanyName());
        d.setJd(dto.getJd());
        d.setDateOfDrive(dto.getDateOfDrive());
        d.setLocation(dto.getLocation());
        User u = userService.findByUsername(principal.getName()); // Admin user
        d.setCreatedBy(u.getId());
        Drive saved = driveService.create(d);
        dto.setId(saved.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{driveId}/apply")
    public ResponseEntity<?> apply(@PathVariable Long driveId, java.security.Principal principal) {
        User u = userService.findByUsername(principal.getName());
        Student s = studentService.findByUser(u);
        Drive d = driveService.findById(driveId);

        if (applicationRepository.existsByStudentAndDrive(s, d)) {
            return ResponseEntity.badRequest().body("You are already applied to this drive");
        }

        Application application = new Application();
        application.setStudent(s);
        application.setDrive(d);
        application.setStatus("APPLIED");
        application.setAppliedAt(OffsetDateTime.now());
        applicationRepository.save(application);
        return ResponseEntity.ok(Map.of("status", "applied"));
    }
    
    @GetMapping("student/upcoming-unapplied")
    public ResponseEntity<List<DriveDTO>> getUnappliedUpcomingDrives(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);
        List<Drive> allDrives = driveService.listAll();

        Set<Long> appliedDriveIds = applicationRepository.findByStudent(student)
                .stream()
                .map(a -> a.getDrive().getId())
                .collect(Collectors.toSet());

        List<DriveDTO> upcomingUnapplied = allDrives.stream()
                .filter(d -> d.getDateOfDrive().isAfter(LocalDate.now()))
                .filter(d -> !appliedDriveIds.contains(d.getId()))
                .map(d -> new DriveDTO(d.getId(), d.getCompanyName(), d.getJd(), d.getDateOfDrive(), d.getLocation()))
                .collect(Collectors.toList());

        System.out.println("Upcoming Unapplied Drives: " + upcomingUnapplied); // Add this line for debug

        return ResponseEntity.ok(upcomingUnapplied);
    }
    // exsting 
 // New API to fetch a single Drive by ID
    @GetMapping("/{id}")
    public ResponseEntity<DriveDTO> getDriveById(@PathVariable Long id) {
        Drive drive = null;
        try {
            drive = driveService.findById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        DriveDTO dto = new DriveDTO(drive.getId(), drive.getCompanyName(), drive.getJd(), drive.getDateOfDrive(), drive.getLocation());
        return ResponseEntity.ok(dto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DriveDTO> updateDrive(@PathVariable Long id, @RequestBody DriveDTO dto) {
        Drive drive = null;
        try {
            drive = driveService.findById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        drive.setCompanyName(dto.getCompanyName());
        drive.setJd(dto.getJd());
        drive.setDateOfDrive(dto.getDateOfDrive());
        drive.setLocation(dto.getLocation());
        driveService.update(drive);
        dto.setId(drive.getId());
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/applied-drives/ids")
    public ResponseEntity<List<Long>> getAppliedDriveIds(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);
        List<Application> applications = applicationRepository.findByStudent(student);
        List<Long> appliedDriveIds = applications.stream()
                .map(a -> a.getDrive().getId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(appliedDriveIds);
    }

}