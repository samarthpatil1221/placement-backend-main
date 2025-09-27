package com.example.placement.service;

import com.example.placement.entity.User;

public interface UserService {
    User registerUser(String username, String password, String role);
    User findByUsername(String username);
}
