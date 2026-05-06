package com.devconnector.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Entity // Tells Hibernate to create a 'users' table
@Table(name = "users")
@Data   // Generates getters and setters (if Lombok is working)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    
	@Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    
    @Column(columnDefinition = "TEXT")
    private String avatar;

    @CreationTimestamp // Automatically sets the date (replaces default: Date.now in MERN)
    private LocalDateTime date;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Prevents infinite recursion during JSON serialization
    private Profile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<Post> posts;
    
    public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public java.util.List<Post> getPosts() {
		return posts;
	}

	public void setPosts(java.util.List<Post> posts) {
		this.posts = posts;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

}