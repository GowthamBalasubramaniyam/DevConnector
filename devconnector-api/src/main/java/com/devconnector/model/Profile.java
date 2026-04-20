package com.devconnector.model;

import jakarta.persistence.*;
import java.util.List;

@Entity

public class Profile {
    @Id
    private Long id; // This matches the User's ID

    @OneToOne
    @MapsId // This links the Profile ID directly to the User ID
    @JoinColumn(name = "user_id")
    private User user;

    private String company;
    private String website;
    private String status;
    
    @ElementCollection // This handles your "skills" array from MERN
    private List<String> skills;

    @Embedded // Includes the Social class we made in Step 1
    private Social social;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Experience> experience;

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