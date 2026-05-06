package com.devconnector.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.devconnector.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> body) {
        // Better way to get the email set by your JwtFilter
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Check for anonymous user manually to avoid DB errors
        if ("anonymousUser".equals(email)) {
            return ResponseEntity.status(401).body("You must be logged in to post");
        }

        try {
            String text = body.get("text");
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.status(400).body("Text is required");
            }
            
            Post post = postService.createPost(email, text);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
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
