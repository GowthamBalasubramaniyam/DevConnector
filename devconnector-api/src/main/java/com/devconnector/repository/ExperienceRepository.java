package com.devconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devconnector.model.Experience;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}
