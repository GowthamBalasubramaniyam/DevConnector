package com.devconnector.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devconnector.model.Comment;
import com.devconnector.model.Post;
import com.devconnector.model.User;
import com.devconnector.repository.CommentRepository;
import com.devconnector.repository.PostRepository;
import com.devconnector.repository.UserRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Post createPost(String email, String text) throws Exception {
        // 1. Find the user from the email provided by the controller
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        // 2. Create and populate the Post object
        Post post = new Post();
        post.setText(text);
        post.setUser(user); // Links the ManyToOne relationship
        post.setName(user.getName()); // For quick display in frontend
        post.setAvatar(user.getAvatar()); // Uses the Base64 TEXT column we fixed
        post.setDate(LocalDateTime.now());

        // 3. Save and return
        return postRepository.save(post);
    }
    
    public String deletePost(String email, Long postId) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        // Security Check: Only the owner can delete
        if (!post.getUser().getId().equals(user.getId())) {
            throw new Exception("User not authorized to delete this post");
        }

        postRepository.delete(post);
        return "Post removed";
    }
    
    @Transactional
    public Post likePost(String email, Long postId) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        Long userId = user.getId();

        // Use an Iterator or removeIf to ensure the ID is actually found and removed
        if (post.getLikes().contains(userId)) {
            // Unlike: Remove the specific ID
            post.getLikes().remove(userId);
        } else {
            // Like: Add the ID
            post.getLikes().add(userId);
        }

        // CRITICAL: Flush the changes so the DB updates before the response is sent
        return postRepository.saveAndFlush(post);
    }
    
    public Post getPostById(Long id) throws Exception {
        return postRepository.findById(id)
                .orElseThrow(() -> new Exception("Post not found"));
    }
    
    public Post addComment(String email, Long postId, String text) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setName(user.getName());
        comment.setAvatar(user.getAvatar());
        comment.setUser(user);
        comment.setPost(post);

        post.getComments().add(comment);
        Post updatedPost = postRepository.save(post);
        updatedPost.getComments().size(); 
        return updatedPost;
    }
    
    public Post deleteComment(String email, Long postId, Long commentId) throws Exception {
        // 1. Find the post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        // 2. Find the comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new Exception("Comment not found"));

        // 3. Security Check: Does this comment belong to the user who is trying to delete it?
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new Exception("User not authorized to delete this comment");
        }

        // 4. Delete from database
        commentRepository.delete(comment);
        
        // 5. Refresh the local post object's list so the response is accurate
        post.getComments().removeIf(c -> c.getId().equals(commentId));

        return post;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByDateDesc();
    }
}
