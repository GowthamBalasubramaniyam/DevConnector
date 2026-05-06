package com.devconnector.repository;

import com.devconnector.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // JpaRepository provides all basic CRUD methods like save(), delete(), and findById()
	@Modifying
    @Transactional
    void deleteByUserId(Long userId);
}