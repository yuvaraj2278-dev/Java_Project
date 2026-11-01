package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "voters")
@Getter @Setter @NoArgsConstructor
public class Voter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_id", unique = true, nullable = false)
    private String voterId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt = LocalDateTime.now();
}
