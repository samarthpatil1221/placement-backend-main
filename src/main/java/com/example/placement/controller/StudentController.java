package com.example.placement.controller;
import com.example.placement.dto.ApplicationDTO;
import com.example.placement.dto.StudentDTO;
import com.example.placement.entity.Application;
import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.repository.ApplicationRepository;
import com.example.placement.service.StudentService;
import com.example.placement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private UserService userService;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Value("${placement.upload.dir}")
    private String uploadDir;

    @GetMapping("/profile")
    public ResponseEntity<StudentDTO> getProfile(Principal principal) {
        User u = userService.findByUsername(principal.getName());
        Student s = studentService.findByUser(u);
        StudentDTO dto = new StudentDTO(s.getId(), u.getUsername(), s.getFullName(), s.getEmail(),
                s.getPhone(), s.getResumePath(), s.getBatch(), s.getCollege());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentDTO> updateProfile(Principal principal, @RequestBody StudentDTO dto) {
        User u = userService.findByUsername(principal.getName());
        Student s = studentService.findByUser(u);
        s.setFullName(dto.getFullName());
        s.setEmail(dto.getEmail());
        s.setPhone(dto.getPhone());
        s.setBatch(dto.getBatch());
        s.setCollege(dto.getCollege());
        studentService.update(s);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/profile/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResume(Principal principal, @RequestParam("file") MultipartFile file) throws Exception {
        User u = userService.findByUsername(principal.getName());
        Student s = studentService.findByUser(u);

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String filename = "student_" + s.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        studentService.updateResumePath(s.getId(), target.toAbsolutePath().toString());
        return ResponseEntity.ok().body(Map.of("path", target.toAbsolutePath().toString()));
    }
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDTO>> getApplications(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);

        List<Application> apps = applicationRepository.findByStudent(student);

        List<ApplicationDTO> dtos = apps.stream()
            .map(app -> new ApplicationDTO(
                app.getId(),
                student != null && student.getFullName() != null ? student.getFullName() : "[UNKNOWN]", // studentName
                app.getStatus(), // status
                app.getDrive() != null && app.getDrive().getCompanyName() != null ? app.getDrive().getCompanyName() : "[UNKNOWN]", // companyName
                app.getAppliedAt() // appliedAt
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

 // In DashboardController (under /api/dashboard)
    @PutMapping("/student/applications/{id}/status")
    public ResponseEntity<?> updateOwnApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Principal principal) {

        String requested = body.getOrDefault("status", "").toUpperCase();

        // Expanded allowed set for students
        if (!List.of("PENDING", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED").contains(requested)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }

        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);

        Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStudent() == null || !app.getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        app.setStatus(requested);
        applicationRepository.save(app);
        return ResponseEntity.ok(Map.of("status","updated"));
    }



    
  
}