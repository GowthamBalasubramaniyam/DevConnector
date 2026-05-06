package com.devconnector.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.devconnector.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByDateDesc(); 
    
    List<Post> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_likes WHERE user_id = :userId", nativeQuery = true)
    void deleteLikesByUserId(Long userId);
}
