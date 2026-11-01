package com.votingsystem.service;

import com.votingsystem.dto.*;
import com.votingsystem.exception.*;
import com.votingsystem.model.*;
import com.votingsystem.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class VoteService {
    private final VoteRepository votes;
    private final ElectionRepository elections;
    private final VoterRepository voters;
    private final ElectionService electionService;
    private final VoterService voterService;

    public VoteService(VoteRepository votes, ElectionRepository elections, VoterRepository voters,
                       ElectionService electionService, VoterService voterService) {
        this.votes = votes;
        this.elections = elections;
        this.voters = voters;
        this.electionService = electionService;
        this.voterService = voterService;
    }

    /** Conflict-proof: business check + DB unique constraint (election_id, voter_id). */
    @Transactional
    public VoteReceipt cast(VoteRequest r) {
        Voter voter = voterService.get(r.voterId());
        Election election = electionService.get(r.electionId());

        if (!"ACTIVE".equals(election.getStatus()))
            throw new VotingException("Voting is not open for this election (" + election.getStatus() + ")");

        Candidate candidate = election.getCandidates().stream()
                .filter(c -> c.getId().equals(r.candidateId())).findFirst()
                .orElseThrow(() -> new VotingException("Candidate does not belong to this election"));

        if (votes.existsByElectionIdAndVoterId(election.getId(), voter.getVoterId()))
            throw new AlreadyVotedException("You have already voted in this election");

        Vote v = new Vote();
        v.setElectionId(election.getId());
        v.setCandidateId(candidate.getId());
        v.setVoterId(voter.getVoterId());
        String code;
        do { code = "VR-" + VoterService.randomCode(8); } while (votes.findByReceiptCode(code).isPresent());
        v.setReceiptCode(code);
        try {
            votes.saveAndFlush(v);
        } catch (DataIntegrityViolationException ex) {
            throw new AlreadyVotedException("You have already voted in this election");
        }
        return new VoteReceipt(code, election.getTitle(), candidate.getName(), v.getCastAt());
    }

    /** Verifies a receipt without revealing who it was cast for. */
    public Map<String, Object> verify(String code) {
        Vote v = votes.findByReceiptCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        Election e = electionService.get(v.getElectionId());
        return Map.of("valid", true, "receiptCode", v.getReceiptCode(),
                "election", e.getTitle(), "castAt", v.getCastAt());
    }

    public ResultsResponse results(Long electionId) {
        Election e = electionService.get(electionId);
        long total = votes.countByElectionId(electionId);
        List<ResultsResponse.CandidateResult> list = e.getCandidates().stream().map(c -> {
            long n = votes.countByCandidateId(c.getId());
            double pct = total == 0 ? 0 : Math.round(n * 1000.0 / total) / 10.0;
            return new ResultsResponse.CandidateResult(c.getId(), c.getName(), c.getParty(), c.getEmoji(), n, pct);
        }).sorted(Comparator.comparingLong(ResultsResponse.CandidateResult::votes).reversed()).toList();
        return new ResultsResponse(e.getId(), e.getTitle(), e.getStatus(), total, list);
    }

    public StatsResponse stats() {
        List<Election> all = elections.findAll();
        List<StatsResponse.RecentVote> recent = votes.findTop10ByOrderByCastAtDesc().stream().map(v -> {
            String title = all.stream().filter(e -> e.getId().equals(v.getElectionId()))
                    .map(Election::getTitle).findFirst().orElse("(deleted)");
            return new StatsResponse.RecentVote(v.getReceiptCode(), title, mask(v.getVoterId()), v.getCastAt());
        }).toList();
        return new StatsResponse(all.size(),
                all.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count(),
                voters.count(), votes.count(), recent);
    }

    private String mask(String id) {
        return id.length() <= 4 ? "****" : id.substring(0, 3) + "***" + id.substring(id.length() - 1);
    }
}
