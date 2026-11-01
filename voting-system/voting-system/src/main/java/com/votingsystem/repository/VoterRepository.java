package com.votingsystem.repository;

import com.votingsystem.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByVoterId(String voterId);
    Optional<Voter> findByEmailIgnoreCase(String email);
    boolean existsByVoterId(String voterId);
}
