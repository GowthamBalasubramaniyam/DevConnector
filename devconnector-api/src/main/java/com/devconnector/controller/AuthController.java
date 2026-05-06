package com.devconnector.controller;

import com.devconnector.model.LoginRequest;
import com.devconnector.model.User;
import com.devconnector.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // This should return the JWT string
            String token = userService.loginUser(loginRequest); 
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return 401 for bad credentials
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // 1. BLOCK ANONYMOUS ACCESS IMMEDIATELY
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal().toString())) {
                System.out.println("Blocked anonymous access attempt");
                return ResponseEntity.status(401).body("Not Authenticated");
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            // 2. Hide password hash for security
            user.setPassword(null); 
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}