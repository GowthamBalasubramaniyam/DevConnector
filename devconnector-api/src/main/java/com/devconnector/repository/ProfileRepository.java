package com.devconnector.repository;

import com.devconnector.model.Profile;
import com.devconnector.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUserEmail(String email);

    // This handles the N+1 performance fix and pagination
    @Query(value = "SELECT DISTINCT p FROM Profile p JOIN FETCH p.user",
           countQuery = "SELECT count(p) FROM Profile p")
    Page<Profile> findAllWithEverything(Pageable pageable);

    // This handles fetching a single profile with all nested data
    @Query("SELECT p FROM Profile p " +
    	       "LEFT JOIN FETCH p.user " + 
    	       "LEFT JOIN FETCH p.experience " + 
    	       "LEFT JOIN FETCH p.education " + 
    	       "WHERE p.user.id = :uId") 
    	Optional<Profile> findByUserIdWithEverything(@Param("uId") Long uId);
}