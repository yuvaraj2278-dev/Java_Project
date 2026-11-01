package com.votingsystem.service;

import com.votingsystem.dto.VoterRegistration;
import com.votingsystem.exception.ResourceNotFoundException;
import com.votingsystem.model.Vote;
import com.votingsystem.model.Voter;
import com.votingsystem.repository.VoteRepository;
import com.votingsystem.repository.VoterRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class VoterService {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    private final VoterRepository voters;
    private final VoteRepository votes;

    public VoterService(VoterRepository voters, VoteRepository votes) {
        this.voters = voters;
        this.votes = votes;
    }

    /** Registers a voter; if the email is already registered, returns the existing record. */
    public Voter register(VoterRegistration r) {
        return voters.findByEmailIgnoreCase(r.email().trim()).orElseGet(() -> {
            Voter v = new Voter();
            v.setName(r.name().trim());
            v.setEmail(r.email().trim().toLowerCase());
            String id;
            do { id = "VT-" + randomCode(6); } while (voters.existsByVoterId(id));
            v.setVoterId(id);
            return voters.save(v);
        });
    }

    public Voter get(String voterId) {
        return voters.findByVoterId(voterId.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Voter ID not found. Please register first."));
    }

    /** IDs of the elections this voter has already voted in (never reveals the choice). */
    public List<Long> votedElections(String voterId) {
        return votes.findByVoterId(get(voterId).getVoterId()).stream().map(Vote::getElectionId).toList();
    }

    static String randomCode(int len) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
        return sb.toString();
    }
}
