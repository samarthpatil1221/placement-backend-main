package com.example.placement.controller;

import com.example.placement.dto.*;
import com.example.placement.entity.Application;
import com.example.placement.entity.Drive;
import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.repository.ApplicationRepository;
import com.example.placement.repository.DriveRepository;
import com.example.placement.repository.StudentRepository;
import com.example.placement.repository.UserRepository;
import com.example.placement.service.DriveService;
import com.example.placement.service.StudentService;
import com.example.placement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;
import com.example.placement.repository.InterviewRepository;



@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DriveService driveService;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserService userService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepository;
    @Autowired
    private InterviewRepository interviewRepository;

    
    private final DriveRepository driveRepo;
    private final ApplicationRepository applicationRepo;
    private final StudentRepository studentRepo;

    public DashboardController(DriveRepository driveRepo, ApplicationRepository applicationRepo, StudentRepository studentRepo) {
        this.driveRepo = driveRepo;
        this.applicationRepo = applicationRepo;
        this.studentRepo = studentRepo;
    }

    /**
     * Student applies to a drive
     */
    @PostMapping("/drives/{driveId}/apply")
    public ResponseEntity<?> applyToDrive(@PathVariable Long driveId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);
        Drive drive = driveService.findById(driveId);

        // Check for existing application
        List<Application> existingApps = applicationRepository.findByStudent(student)
            .stream()
            .filter(a -> a.getDrive().getId().equals(driveId))
            .collect(Collectors.toList());

        if (applicationRepository.existsByStudentAndDrive(student, drive)) {
            return ResponseEntity.badRequest().body("You are already applied to this drive");
        }

        Application app = new Application();
        app.setStudent(student);
        app.setDrive(drive);
        app.setStatus("APPLIED");
        app.setAppliedAt(OffsetDateTime.now());

        applicationRepository.save(app);

        return ResponseEntity.ok("Application submitted successfully");
    }


    /**
     * Student Dashboard
     */
    @GetMapping("/student")
    public ResponseEntity<DashboardStatsDTO> getStudentDashboard(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);

        List<Drive> allDrives = driveService.listAll();

        // Filter to upcoming drives student has not applied to
        Set<Long> appliedDriveIds = applicationRepository.findByStudent(student)
                .stream()
                .map(a -> a.getDrive().getId())
                .collect(Collectors.toSet());

        int availableDrives = (int) allDrives.stream()
                .filter(d -> d.getDateOfDrive() != null && d.getDateOfDrive().isAfter(LocalDate.now()))
                .filter(d -> !appliedDriveIds.contains(d.getId()))
                .count();

        List<Application> applications = applicationRepository.findByStudent(student);

        int totalApplications = applications.size();
        int acceptedCount = (int) applications.stream().filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus())).count();
        int pendingCount = (int) applications.stream().filter(a ->
                "PENDING".equalsIgnoreCase(a.getStatus()) || "APPLIED".equalsIgnoreCase(a.getStatus())).count();
        int interviewCount = (int) applications.stream().filter(a -> "INTERVIEW".equalsIgnoreCase(a.getStatus())).count();
        int rejectedCount = (int) applications.stream().filter(a -> "REJECTED".equalsIgnoreCase(a.getStatus())).count();

        // Last 3 recent drives applied
        List<DriveDTO> recentDrives = applications.stream()
                .sorted((a1, a2) -> a2.getAppliedAt().compareTo(a1.getAppliedAt()))
                .limit(3)
                .map(a -> new DriveDTO(a.getDrive().getId(),
                        a.getDrive().getCompanyName(),
                        a.getDrive().getJd(),
                        a.getDrive().getDateOfDrive(),
                        a.getDrive().getLocation()))
                .collect(Collectors.toList());

        List<ApplicationStatusDTO> applicationStatus = List.of(
                new ApplicationStatusDTO("Accepted", acceptedCount, "#24b47e", percentage(acceptedCount, totalApplications)),
                new ApplicationStatusDTO("Pending", pendingCount, "#ffc200", percentage(pendingCount, totalApplications)),
                new ApplicationStatusDTO("Interview", interviewCount, "#1976d2", percentage(interviewCount, totalApplications)),
                new ApplicationStatusDTO("Rejected", rejectedCount, "#fa5252", percentage(rejectedCount, totalApplications))
        );

        List<ApplicationDTO> applicationsList = applications.stream()
                .map(a -> new ApplicationDTO(
                        a.getId(),
                        a.getStudent() != null ? a.getStudent().getFullName() : "[UNKNOWN]",  // studentName
                        a.getStatus(),                                                       // status
                        a.getDrive() != null ? a.getDrive().getCompanyName() : "[UNKNOWN]",  // companyName
                        a.getAppliedAt()                                                     // appliedAt
                )
)
                .collect(Collectors.toList());

        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setAvailableDrives(availableDrives);
        dto.setApplicationsCount(totalApplications);
        dto.setAcceptedCount(acceptedCount);
        dto.setPendingCount(pendingCount);
        dto.setInterviewCount(interviewCount);
        dto.setTotalApplications(totalApplications);
        dto.setRecentDrives(recentDrives);
        dto.setApplicationStatus(applicationStatus);
        dto.setApplicationsList(applicationsList);

        return ResponseEntity.ok(dto);
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



    /**
     * Admin Dashboard
     */
    @GetMapping("/admin")
    public ResponseEntity<Map<String,Object>> getAdminDashboard() {
        Map<String,Object> result = new HashMap<>();

        // All students
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("STUDENT")))
                .collect(Collectors.toList());

        // All drives
        List<Drive> drives = driveService.listAll();

        // All applications
        List<Application> apps = applicationRepository.findAll();

        // Count stats
        result.put("totalStudents", students.size());
        result.put("totalDrives", drives.size());
        result.put("totalApplications", apps.size());

        // Company-wise stats
        List<Map<String,Object>> companyStats = drives.stream().map(d -> {
            int applied = (int) apps.stream().filter(a -> a.getDrive().getId().equals(d.getId())).count();
            Map<String,Object> map = new HashMap<>();
            map.put("companyName", d.getCompanyName());
            map.put("appliedCount", applied);
            map.put("notAppliedCount", students.size() - applied);
            map.put("date", d.getDateOfDrive());
            return map;
        }).collect(Collectors.toList());

        result.put("companyStats", companyStats);

        // Upcoming drives (next 3)
        result.put("upcomingDrives", drives.stream()
                .filter(d -> d.getDateOfDrive().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Drive::getDateOfDrive))
                .limit(3)
                .map(d -> new DriveDTO(
                        d.getId(),
                        d.getCompanyName(),
                        d.getJd(),
                        d.getDateOfDrive(),
                        d.getLocation()))
                .toList());

        return ResponseEntity.ok(result);
    }

    private double percentage(int value, int total) {
        return total == 0 ? 0 : (value * 100.0 / total);
    }
    
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats1(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Student student = studentService.findByUser(user);
        Long studentId = student.getId();

        // Calculate stats
        int totalDrives = driveRepo.countAllDrives();
        int totalApplied = applicationRepo.countApplicationsByStudentId(studentId);
        int skillsCount = getSkillsCountForStudent(studentId);
        int profileCompletion = calculateProfileCompletion(studentId);

        System.out.println("Dashboard Stats for studentId=" + studentId);
        System.out.println("totalDrives=" + totalDrives);
        System.out.println("totalApplied=" + totalApplied);
        System.out.println("skillsCount=" + skillsCount);
        System.out.println("profileCompletion=" + profileCompletion);

        // build DTO
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setTotalDrives(totalDrives);
        dto.setApplicationsCount(totalApplied);
        dto.setSkillsCount(skillsCount);
        dto.setProfileCompletion(profileCompletion);

        return ResponseEntity.ok(dto);
    }

    // Placeholder dummy methods
    private int getSkillsCountForStudent(Long studentId) {
        // TODO: Implement actual skill count logic
        return 5; // example static value
    }

    private int calculateProfileCompletion(Long studentId) {
        // TODO: Implement actual profile completion calculation
        return 75; // example static value in percentage
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