package com.devconnector.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Social {
    private String twitter;
    private String facebook;
    private String linkedin;
    private String youtube;
    private String instagram;
	public String getTwitter() {
		return twitter;
	}
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
	public String getLinkedin() {
		return linkedin;
	}
	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}
	public String getYoutube() {
		return youtube;
	}
	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
	public String getInstagram() {
		return instagram;
	}
	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}
    
}