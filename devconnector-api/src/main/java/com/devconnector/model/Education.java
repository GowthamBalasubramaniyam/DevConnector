package com.devconnector.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school;
    private String degree;
    private String fieldofstudy;
    @JsonProperty("from")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate; 
    @JsonProperty("to")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDate;   
    private boolean current;
    private String description;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getFieldofstudy() {
		return fieldofstudy;
	}

	public void setFieldofstudy(String fieldofstudy) {
		this.fieldofstudy = fieldofstudy;
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