package com.devconnector.controller;

import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devconnector.model.Post;
import com.devconnector.service.AiModerationService;
import com.devconnector.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AiModerationService aiModerationService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> body) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 1. Authentication check
        if ("anonymousUser".equals(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("msg", "You must be logged in to post"));
        }

        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Map.of("msg", "Text is required"));
        }

        try {
            // 2. AI Gatekeeper
            // We call this BEFORE the service layer to save database resources
            if (!aiModerationService.isTechRelated(text)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", "Content Rejected: Dev-Verse is for software development and tech-related posts only."));
            }
            
            // 3. Persist
            Post post = postService.createPost(email, text);
            return ResponseEntity.ok(post);
            
        } catch (AccountNotFoundException e) {
            // Catching specific business exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("msg", e.getMessage()));
        } catch (Exception e) {
            // Log the technical error so you can see it in Render logs
            System.err.println("Unexpected error during post creation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("msg", "An internal server error occurred."));
        }
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            String message = postService.deletePost(email, id);
            return ResponseEntity.ok(Map.of("msg", message));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("msg", e.getMessage()));
        }
    }
    
    @PutMapping("/like/{id}")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
    	String email = SecurityContextHolder.getContext().getAuthentication().getName();
    	try {
            Post updatedPost = postService.likePost(email, id);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
    @PostMapping("/comment/{id}")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            Post updatedPost = postService.addComment(email, id, body.get("text"));
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Post not found");
        }
    }
    
    @DeleteMapping("/comment/{id}/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @PathVariable Long comment_id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            Post updatedPost = postService.deleteComment(email, id, comment_id);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
}
