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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        Post post = new Post();
        post.setText(text);
        post.setUser(user); 
        post.setName(user.getName()); 
        post.setAvatar(user.getAvatar()); 
        post.setDate(LocalDateTime.now());

        return postRepository.save(post);
    }
    
    public String deletePost(String email, Long postId) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        // Only the owner can delete
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

        if (post.getLikes().contains(userId)) {
            // Unlike: Remove the specific ID
            post.getLikes().remove(userId);
        } else {
            // Like: Add the ID
            post.getLikes().add(userId);
        }

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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new Exception("Post not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new Exception("Comment not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new Exception("User not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        
        post.getComments().removeIf(c -> c.getId().equals(commentId));

        return post;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByDateDesc();
    }
}
