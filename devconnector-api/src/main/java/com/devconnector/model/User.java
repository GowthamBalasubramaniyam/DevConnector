package com.devconnector.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
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
    private String password;

    private String avatar;

    @CreationTimestamp // Automatically sets the date (replaces default: Date.now in MERN)
    private LocalDateTime date;
}