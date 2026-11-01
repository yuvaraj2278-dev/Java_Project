package com.votingsystem.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elections")
@Getter @Setter @NoArgsConstructor
public class Election {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String category;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    private boolean closed = false;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Candidate> candidates = new ArrayList<>();

    public void addCandidate(Candidate c) {
        c.setElection(this);
        candidates.add(c);
    }

    /** UPCOMING, ACTIVE or CLOSED (computed from the schedule). */
    public String getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (closed || now.isAfter(endTime)) return "CLOSED";
        if (now.isBefore(startTime)) return "UPCOMING";
        return "ACTIVE";
    }
}
