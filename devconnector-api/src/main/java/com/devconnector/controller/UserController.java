package com.devconnector.controller;

import com.devconnector.model.User;
import com.devconnector.service.UserService;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
    	System.out.println("Registering user: " + user.getEmail() + " with password: " + user.getPassword());
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // This retrieves the email we stored in the SecurityContext during the JwtFilter step
        Object principal = org.springframework.security.core.context.SecurityContextHolder
                            .getContext().getAuthentication().getPrincipal();
        
        return ResponseEntity.ok("You are logged in as: " + principal.toString());
    }
    

}