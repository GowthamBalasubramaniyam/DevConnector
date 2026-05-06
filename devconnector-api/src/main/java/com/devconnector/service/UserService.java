package com.devconnector.service;

import com.devconnector.model.LoginRequest;
import com.devconnector.model.Post;
import com.devconnector.model.User;
import com.devconnector.repository.CommentRepository;
import com.devconnector.repository.PostRepository;
import com.devconnector.repository.ProfileRepository;
import com.devconnector.repository.UserRepository;
import com.devconnector.security.JwtUtils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    public User registerUser(User user) throws Exception {
        // 1. Check if user exists (replaces your 'User already exists' error)
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("User already exists");
        }

        // 2. Hash the password (replaces bcrypt logic)
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Save user
        return userRepository.save(user);
    }
    @Autowired
    private JwtUtils jwtUtils; // The class we created in the last step

    @Transactional(readOnly = true)
    public String loginUser(LoginRequest loginRequest) throws Exception {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new Exception("Invalid Credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Invalid Credentials");
        }

        return jwtUtils.generateToken(user.getEmail());
    }
    
    public User findByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
        
        // Create a new User object to send to the frontend 
        // so we don't accidentally "null out" the real password in the DB
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setName(user.getName());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setAvatar(user.getAvatar());
        
        return cleanUser;
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        // 1. Manually remove this user's ID from all 'post_likes' tables across the app
        // This prevents "Ghost Likes" from a deleted user
    	postRepository.deleteLikesByUserId(userId);
    	profileRepository.deleteById(userId);

    	commentRepository.deleteByUserId(userId);
        // 2. Now delete the user (which triggers cascade for their own Profile/Posts)
        userRepository.deleteById(userId);
    }
    
}