package com.votingsystem.repository;

import com.votingsystem.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByElectionIdAndVoterId(Long electionId, String voterId);
    long countByElectionId(Long electionId);
    long countByCandidateId(Long candidateId);
    Optional<Vote> findByReceiptCode(String receiptCode);
    List<Vote> findByVoterId(String voterId);
    List<Vote> findTop10ByOrderByCastAtDesc();
    void deleteByElectionId(Long electionId);
}
