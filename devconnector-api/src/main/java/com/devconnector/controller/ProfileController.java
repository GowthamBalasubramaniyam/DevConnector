package com.devconnector.controller;

import com.devconnector.dto.ProfileDTO;
import com.devconnector.model.Education;
import com.devconnector.model.Experience;
import com.devconnector.model.Profile;
import com.devconnector.model.Social;
import com.devconnector.model.User;
import com.devconnector.service.ProfileService;
import com.devconnector.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000") // Ready for React connection
public class ProfileController {

    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> saveProfile(@RequestBody Map<String, Object> profileData) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            // Convert the incoming Map to your Profile object manually or via a DTO
            Profile profile = new Profile();
            
            Object socialObj = profileData.get("social");
            if (socialObj instanceof Map<?, ?>) {
                Map<?, ?> socialMap = (Map<?, ?>) socialObj;
                Social social = new Social();
                social.setTwitter((String) socialMap.get("twitter"));
                social.setFacebook((String) socialMap.get("facebook"));
                social.setLinkedin((String) socialMap.get("linkedin"));
                social.setYoutube((String) socialMap.get("youtube"));
                social.setInstagram((String) socialMap.get("instagram"));
                profile.setSocial(social); // Set it on the profile object
            }
            
            Object skillsObj = profileData.get("skills");
            List<String> skillsList = new ArrayList<>();

            if (skillsObj != null) {
                if (skillsObj instanceof String && !((String) skillsObj).trim().isEmpty()) {
                    // Handle case where it arrives as a string
                    skillsList = Arrays.stream(((String) skillsObj).split(","))
                                       .map(String::trim)
                                       .filter(s -> !s.isEmpty())
                                       .collect(Collectors.toList());
                } else if (skillsObj instanceof List<?>) {
                    // Handle case where it arrives as a JSON Array (your React action does this)
                    for (Object item : (List<?>) skillsObj) {
                        if (item instanceof String) {
                            skillsList.add((String) item);
                        }
                    }
                }
            }

            System.out.println("DEBUG: Final parsed skills: " + skillsList);
            profile.setSkills(skillsList);

            // Set other fields...
            profile.setCompany((String) profileData.get("company"));
            profile.setWebsite((String) profileData.get("website"));
            profile.setLocation((String) profileData.get("location"));
            profile.setStatus((String) profileData.get("status"));
            
            // These are the two you were missing:
            profile.setBio((String) profileData.get("bio"));
            profile.setGithubusername((String) profileData.get("githubusername"));
            System.out.println("DEBUG: Skills to save: " + profile.getSkills());
            Profile savedProfile = profileService.createOrUpdateProfile(email, profile);
            return ResponseEntity.ok(profileService.convertToDTO(savedProfile));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.status(400).body("Please select an image file");
        if (file.getSize() > 1024 * 1024) return ResponseEntity.status(400).body("File size exceeds 1MB");

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String dataUrl = "data:" + file.getContentType() + ";base64," + base64Image;

            profileService.updateAvatar(email, dataUrl);
            return ResponseEntity.ok(Map.of("avatar", dataUrl, "msg", "Avatar uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error");
        }
    }

    @PutMapping("/experience")
    public ResponseEntity<?> addExperience(@RequestBody Experience experience) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            Profile updatedProfile = profileService.addExperience(email, experience);
            return ResponseEntity.ok(profileService.convertToDTO(updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/education")
    public ResponseEntity<?> addEducation(@RequestBody Education education) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("Education From Date Received: " + education.getFromDate());
        try {
            Profile updatedProfile = profileService.addEducation(email, education);
            return ResponseEntity.ok(profileService.convertToDTO(updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/education/{edu_id}")
    public ResponseEntity<?> deleteEducation(@PathVariable("edu_id") Long eduId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            Profile updatedProfile = profileService.deleteEducation(email, eduId);
            return ResponseEntity.ok(profileService.convertToDTO(updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/experience/{exp_id}")
    public ResponseEntity<?> deleteExperience(@PathVariable("exp_id") Long expId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            Profile updatedProfile = profileService.deleteExperience(email, expId);
            return ResponseEntity.ok(profileService.convertToDTO(updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    public Page<ProfileDTO> getAllProfiles(Pageable pageable) {
        return profileService.getAllProfiles(pageable);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getProfileByUserId(@PathVariable Long user_id) {
        try {
            // Your existing service method
            ProfileDTO profile = profileService.getProfileByUserId(user_id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
        	System.out.println("Profile fetch failed for user: " + user_id);
            return ResponseEntity.status(404).body("Profile not found");
        }
    }
    
 // Add this inside ProfileController.java

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentProfile() {
        try {
            // 1. Get the email from the SecurityContext
            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            
            // 2. Fetch the profile using the service
            Profile profile = profileService.getProfileByEmail(email);
            
            if (profile == null) {
                return ResponseEntity.status(404).body("There is no profile for this user");
            }
            
            // 3. Return the DTO (to match what React expects)
            return ResponseEntity.ok(profileService.convertToDTO(profile));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error");
        }
    }

    @GetMapping("/github/{username}")
    public ResponseEntity<?> getGithubRepos(@PathVariable String username) {
        try {
            Object repos = profileService.getGithubRepos(username);
            return ResponseEntity.ok(repos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("No Github profile found");
        }
    }
    
 // In ProfileController.java
    @DeleteMapping
    public ResponseEntity<?> deleteAccount() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            User user = userService.findByEmail(email);
            
            System.out.println("Deleting User ID: " + user.getId());
            
            // This triggers the service logic we just fixed
            userService.deleteUser(user.getId());

            return ResponseEntity.ok(Map.of("msg", "Account Deleted"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting account");
        }
    }
}