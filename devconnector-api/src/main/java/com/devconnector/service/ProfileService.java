package com.devconnector.service;

import com.devconnector.model.Profile;
import com.devconnector.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }
    public Profile createOrUpdateProfile(Long userId, Profile profileData, List<String> skills) {
        // 1. Check if profile already exists (Like your findOne in MERN)
        return profileRepository.findByUserId(userId)
            .map(existingProfile -> {
                // 2. Update existing profile fields
                existingProfile.setCompany(profileData.getCompany());
                existingProfile.setWebsite(profileData.getWebsite());
                existingProfile.setStatus(profileData.getStatus());
                existingProfile.setId(profileData.getId());
                existingProfile.setSkills(skills); // We pass the split list here
                existingProfile.setSocial(profileData.getSocial());
                return profileRepository.save(existingProfile);
            })
            .orElseGet(() -> {
                // 3. If no profile, create a new one
                profileData.setSkills(skills);
                return profileRepository.save(profileData);
            });
    }
}