package com.devconnector.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    

	private String company;
    private String location;
    
    @JsonProperty("from")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate; // Java uses LocalDate for dates
    
    @JsonProperty("to")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDate;
    private boolean current;
    private String description;

    @ManyToOne // Many experiences belong to one Profile
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profile;
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@JsonProperty("from")
	public LocalDate getFromDate() {
		return fromDate;
	}
	@JsonProperty("from")
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	@JsonProperty("to")
	public LocalDate getToDate() {
		return toDate;
	}
	@JsonProperty("to")
	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}