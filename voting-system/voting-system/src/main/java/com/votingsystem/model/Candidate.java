package com.votingsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "candidates")
@Getter @Setter @NoArgsConstructor
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String party;

    private String emoji;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    @JsonBackReference
    private Election election;

    public Candidate(String name, String party, String emoji) {
        this.name = name;
        this.party = party;
        this.emoji = emoji;
    }
}
