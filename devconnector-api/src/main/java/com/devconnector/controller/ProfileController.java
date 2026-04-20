package com.devconnector.controller;

import com.devconnector.model.Profile;
import com.devconnector.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile") // This matches your MERN route base
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // @route   POST api/profile
    // @desc    Create or update user profile
    @PostMapping
    public ResponseEntity<Profile> saveProfile(@RequestBody Profile profileRequest, @RequestParam String skillsInput) {
        
        // Convert the comma-separated string to a List (Replaces skills.split(','))
        List<String> skills = Arrays.stream(skillsInput.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toList());

        // For now, we manually pass a User ID (we will add JWT Auth later)
        Long mockUserId = 1L; 
        
        Profile savedProfile = profileService.createOrUpdateProfile(mockUserId, profileRequest, skills);
        return ResponseEntity.ok(savedProfile);
    }
}