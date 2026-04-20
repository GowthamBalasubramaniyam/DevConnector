package com.devconnector.service;

import com.devconnector.model.LoginRequest;
import com.devconnector.model.User;
import com.devconnector.repository.UserRepository;
import com.devconnector.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) throws Exception {
        // 1. Check if user exists (replaces your 'User already exists' error)
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("User already exists");
        }

        // 2. Hash the password (replaces bcrypt logic)
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Save user
        return userRepository.save(user);
    }
    @Autowired
    private JwtUtils jwtUtils; // The class we created in the last step

    public String loginUser(LoginRequest loginRequest) throws Exception {
        // 1. Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new Exception("Invalid Credentials"));

        // 2. Check password (replaces bcrypt.compare)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Invalid Credentials");
        }

        // 3. Return the JWT
        return jwtUtils.generateToken(user.getEmail());
    }
}