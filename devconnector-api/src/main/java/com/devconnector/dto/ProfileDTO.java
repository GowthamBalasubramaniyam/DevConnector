package com.devconnector.dto;

import java.util.List;

import com.devconnector.model.Education;
import com.devconnector.model.Experience;
import com.devconnector.model.Social;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProfileDTO {
    private Long id;
    private String company;
    private String website;
    private String status;
    private List<String> skills;
    
    @JsonProperty("bio")
    private String bio;
    private String githubusername;
    
    // Flattened User Info
    private String name;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public List<String> getSkills() {
		return skills;
	}
	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getGithubusername() {
		return githubusername;
	}
	public void setGithubusername(String githubusername) {
		this.githubusername = githubusername;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Experience> getExperience() {
		return experience;
	}
	public void setExperience(List<Experience> experience) {
		this.experience = experience;
	}
	public List<Education> getEducation() {
		return education;
	}
	public void setEducation(List<Education> education) {
		this.education = education;
	}
	private String avatar;
    private String email;

    private List<Experience> experience;
    private List<Education> education;
    private Social social;
    
    @JsonProperty("Location")
    private String Location;
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	public Social getSocial() {
		return social;
	}
	public void setSocial(Social social) {
		this.social = social;
	}
}