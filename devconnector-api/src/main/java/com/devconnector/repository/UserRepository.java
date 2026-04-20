package com.devconnector.repository;

import com.devconnector.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // This replaces: User.findOne({ email })
    Optional<User> findByEmail(String email);
}