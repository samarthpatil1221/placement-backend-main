package com.example.placement.controller;

import com.example.placement.dto.UserDTO;
import com.example.placement.dto.UserRegistrationDTO;
import com.example.placement.entity.Student;
import com.example.placement.entity.User;
import com.example.placement.service.StudentService;
import com.example.placement.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO dto) {
        try {
            User u = userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getRole());
            // if student role, create student profile row
            if ("STUDENT".equalsIgnoreCase(dto.getRole())) {
                studentService.createForUser(u);
            }
            return ResponseEntity.ok(new UserDTO(u.getId(), u.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        try {
            request.login(username, password); // container-managed authentication
            return ResponseEntity.ok().body(Map.of("status", "success"));
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String username = auth.getName();
        User u = userService.findByUsername(username);
        return ResponseEntity.ok(Map.of(
            "id", u.getId(),
            "username", u.getUsername(),
            "roles", u.getRoles().stream().map(r -> r.getName()).toArray()
        ));
    }


}
