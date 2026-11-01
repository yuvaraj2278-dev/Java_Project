package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** One ballot. The unique constraint guarantees one vote per voter per election. */
@Entity
@Table(name = "votes",
        uniqueConstraints = @UniqueConstraint(name = "uk_one_vote", columnNames = {"election_id", "voter_id"}))
@Getter @Setter @NoArgsConstructor
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "voter_id", nullable = false)
    private String voterId;

    @Column(name = "receipt_code", unique = true, nullable = false)
    private String receiptCode;

    @Column(name = "cast_at")
    private LocalDateTime castAt = LocalDateTime.now();
}
