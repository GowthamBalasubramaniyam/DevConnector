package com.devconnector.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable // This tells Spring: "This is not a separate table, just part of the Profile"
@Data
public class Social {
    private String youtube;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
}