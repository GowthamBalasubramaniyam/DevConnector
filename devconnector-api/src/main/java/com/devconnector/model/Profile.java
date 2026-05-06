package com.devconnector.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity

public class Profile {
    @Id
    private Long id; 

    @OneToOne
    @MapsId 
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String company;
    private String website;
    private String status;
    
    @ElementCollection
    @CollectionTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_user_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "twitter", column = @Column(name = "social_twitter")),
        @AttributeOverride(name = "facebook", column = @Column(name = "social_facebook")),
        @AttributeOverride(name = "linkedin", column = @Column(name = "social_linkedin")),
        @AttributeOverride(name = "youtube", column = @Column(name = "social_youtube")),
        @AttributeOverride(name = "instagram", column = @Column(name = "social_instagram"))
    })
    private Social social;
    
    private String githubusername;
    private String bio;
    
    private String location;
    

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) { 
        this.location = location;
    }
    
    public String getGithubusername() {
        return githubusername;
    }

    public void setGithubusername(String githubusername) {
        this.githubusername = githubusername;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experience = new ArrayList<>();
    
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> education = new ArrayList<>();

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public Social getSocial() {
		return social;
	}

	public void setSocial(Social social) {
		this.social = social;
	}

	public List<Experience> getExperience() {
		return experience;
	}

	public void setExperience(List<Experience> experience) {
		this.experience = experience;
	}
}