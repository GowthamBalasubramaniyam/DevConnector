package com.devconnector.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    // We store these just like your MERN app for quick display
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ElementCollection(fetch = FetchType.EAGER) // EAGER ensures other users see it immediately on load
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id")
    private Set<Long> likes = new HashSet<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Set<Long> getLikes() {
		return likes;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setLikes(Set<Long> likes) {
		this.likes = likes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	@Column(name = "created_at")
	private LocalDateTime date = LocalDateTime.now();
	
    // We will add Likes and Comments relationships in the next steps
}