package com.devconnector.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;
    private LocalDate fromDate; // Note: Java uses LocalDate for dates
    private LocalDate toDate;
    private boolean current;
    private String description;

    @ManyToOne // Many experiences belong to one Profile
    @JoinColumn(name = "profile_id")
    private Profile profile;
}