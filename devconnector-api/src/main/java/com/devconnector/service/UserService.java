package com.devconnector.service;

import com.devconnector.model.LoginRequest;
import com.devconnector.model.User;
import com.devconnector.repository.CommentRepository;
import com.devconnector.repository.PostRepository;
import com.devconnector.repository.ProfileRepository;
import com.devconnector.repository.UserRepository;
import com.devconnector.security.JwtUtils;

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
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
    @Autowired
    private JwtUtils jwtUtils; 

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
        
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setName(user.getName());
        cleanUser.setEmail(user.getEmail());
        cleanUser.setAvatar(user.getAvatar());
        
        return cleanUser;
    }
    
    @Transactional
    public void deleteUser(Long userId) {
    	postRepository.deleteLikesByUserId(userId);
    	profileRepository.deleteById(userId);

    	commentRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
    
}