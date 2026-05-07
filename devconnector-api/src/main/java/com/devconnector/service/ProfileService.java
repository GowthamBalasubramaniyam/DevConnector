package com.devconnector.service;

import com.devconnector.dto.ProfileDTO;
import com.devconnector.model.Education;
import com.devconnector.model.Experience;
import com.devconnector.model.Profile;
import com.devconnector.model.User;
import com.devconnector.repository.EducationRepository;
import com.devconnector.repository.ExperienceRepository;
import com.devconnector.repository.ProfileRepository;
import com.devconnector.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    // 1. Create or Update Profile
    public Profile createOrUpdateProfile(String email, Profile profileData) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        Optional<Profile> existingProfile = profileRepository.findByUser(user);

        if (existingProfile.isPresent()) {
            Profile profileToUpdate = existingProfile.get();
            profileToUpdate.setCompany(profileData.getCompany());
            profileToUpdate.setWebsite(profileData.getWebsite());
            profileToUpdate.setStatus(profileData.getStatus());
            profileToUpdate.getSkills().clear();
            if (profileData.getSkills() != null) {
                profileToUpdate.getSkills().addAll(profileData.getSkills());
            }
            profileToUpdate.setSocial(profileData.getSocial());
            profileToUpdate.setGithubusername(profileData.getGithubusername());
            profileToUpdate.setBio(profileData.getBio());
            profileToUpdate.setLocation(profileData.getLocation());
            return profileRepository.save(profileToUpdate);
        }

        profileData.setUser(user);
        return profileRepository.save(profileData);
    }
    
    public Profile getProfileByEmail(String email) {
        return profileRepository.findByUserEmail(email)
                .orElse(null); 
    }

    // 2. Add Experience
    public Profile addExperience(String email, Experience exp) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new Exception("Profile not found"));
        
        exp.setProfile(profile);
        experienceRepository.save(exp);
        return profile;
    }

    // 3. Add Education
    public Profile addEducation(String email, Education edu) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new Exception("Profile not found"));
        
        edu.setProfile(profile);
        educationRepository.save(edu);
        return profile;
    }

    // 4. Delete Education
    public Profile deleteEducation(String email, Long eduId) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new Exception("Profile not found"));
        
        Education education = educationRepository.findById(eduId)
                .orElseThrow(() -> new Exception("Education record not found"));
        
        if (!education.getProfile().getId().equals(profile.getId())) {
            throw new Exception("Unauthorized to delete this record");
        }
        
        educationRepository.delete(education);
        return profile;
    }

    // 5. Delete Experience
    public Profile deleteExperience(String email, Long expId) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        Profile profile = profileRepository.findByUser(user).orElseThrow(() -> new Exception("Profile not found"));
        
        Experience experience = experienceRepository.findById(expId)
                .orElseThrow(() -> new Exception("Experience not found"));
        
        if (!experience.getProfile().getId().equals(profile.getId())) {
            throw new Exception("Unauthorized to delete this experience");
        }
        
        experienceRepository.delete(experience);
        return profile;
    }

    // 6. Get All Profiles (Paginated)
    @Transactional(readOnly = true)
    public Page<ProfileDTO> getAllProfiles(Pageable pageable) {
        return profileRepository.findAllWithEverything(pageable)
                .map(this::convertToDTO);
    }

    // 7. Get Profile By User ID
    public ProfileDTO getProfileByUserId(Long userId) throws Exception {
        // 1. Find the User first (this ensures the ID is valid)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));

        // 2. Use the built-in findByUser method (we saw this in your Repository)
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new Exception("Profile not found for user: " + user.getName()));

        return convertToDTO(profile);
    }

    // 8. Update Avatar
    public void updateAvatar(String email, String dataUrl) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
        user.setAvatar(dataUrl);
        userRepository.save(user);
    }

    // 9. GitHub Repos (External API)
    public Object getGithubRepos(String username) {
        // 1. Correct GitHub API URL
        String url = "https://api.github.com/users/" + username + "/repos?per_page=5&sort=created:asc";
        
        // 2. You MUST set a User-Agent header or GitHub returns 403/404
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Dev-Verse-App"); 
        headers.set("Accept", "application/vnd.github.v3+json");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 3. Use exchange() to include the headers in the GET request
            ResponseEntity<Object> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Object.class
            );
            return response.getBody();
        } catch (Exception e) {
            // 4. This prints the REAL error to your Render logs (Check them!)
            System.err.println("GitHub Fetch Error: " + e.getMessage());
            return null; 
        }
    }

    // 10. Mapper (Public so Controller can use it for POST/PUT returns)
    public ProfileDTO convertToDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setCompany(profile.getCompany());
        dto.setWebsite(profile.getWebsite());
        dto.setStatus(profile.getStatus());
        dto.setSkills(profile.getSkills());
        dto.setGithubusername(profile.getGithubusername());
        dto.setBio(profile.getBio());
        dto.setLocation(profile.getLocation());

        if (profile.getUser() != null) {
            dto.setName(profile.getUser().getName());
            dto.setAvatar(profile.getUser().getAvatar());
            dto.setEmail(profile.getUser().getEmail());
        }

        dto.setExperience(profile.getExperience());
        dto.setEducation(profile.getEducation());
        dto.setSocial(profile.getSocial());
        return dto;
    }
}