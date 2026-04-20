package com.devconnector.repository;

import com.devconnector.model.Profile;
import com.devconnector.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    // This replaces: Profile.findOne({ user: req.user.id })
    Optional<Profile> findByUser(User user);
    
    // This allows you to find a profile by the User ID directly
    Optional<Profile> findByUserId(Long userId);
}