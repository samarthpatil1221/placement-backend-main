package com.example.placement.config;

import com.example.placement.entity.Role;
import com.example.placement.entity.User;
import com.example.placement.repository.RoleRepository;
import com.example.placement.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role(null,"ADMIN")));
        Role student = roleRepository.findByName("STUDENT").orElseGet(() -> roleRepository.save(new Role(null,"STUDENT")));

        if (userRepository.findByUsername("admin").isEmpty()) {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setEnabled(true);
            u.getRoles().add(admin);
            userRepository.save(u);
            System.out.println("Created default admin (username=admin, password=admin123)");
        } else {
            System.out.println("Admin user already exists");
        }
    }

}
