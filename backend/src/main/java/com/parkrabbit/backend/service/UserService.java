package com.parkrabbit.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.parkrabbit.backend.config.JwtUtils;
import com.parkrabbit.backend.entity.User;
import com.parkrabbit.backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public String registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return jwtUtils.generateToken(user);
    }

    public String loginUser(String username, String password) {
        // 1. Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found!"));

        // 2. Check if password matches the hashed version in DB
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Error: Invalid password!");
        }

        // 3. If successful, generate and return a new token
        return jwtUtils.generateToken(user);
    }
}