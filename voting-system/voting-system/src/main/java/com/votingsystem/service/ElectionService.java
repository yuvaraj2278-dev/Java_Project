package com.votingsystem.service;

import com.votingsystem.dto.ElectionRequest;
import com.votingsystem.exception.ResourceNotFoundException;
import com.votingsystem.exception.VotingException;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ElectionService {
    private static final String[] EMOJIS = {"🦁", "🦅", "🐺", "🦊", "🐼", "🐯", "🦉", "🐬"};

    private final ElectionRepository elections;
    private final VoteRepository votes;

    public ElectionService(ElectionRepository elections, VoteRepository votes) {
        this.elections = elections;
        this.votes = votes;
    }

    public List<Election> findAll(String category, String search) {
        return elections.findAllByOrderByStartTimeDesc().stream()
                .filter(e -> category == null || category.isBlank() || category.equalsIgnoreCase(e.getCategory()))
                .filter(e -> search == null || search.isBlank()
                        || e.getTitle().toLowerCase().contains(search.toLowerCase()))
                .toList();
    }

    public Election get(Long id) {
        return elections.findById(id).orElseThrow(() -> new ResourceNotFoundException("Election not found: " + id));
    }

    @Transactional
    public Election create(ElectionRequest r) {
        if (!r.endTime().isAfter(r.startTime())) throw new VotingException("End time must be after start time");
        Election e = new Election();
        e.setTitle(r.title().trim());
        e.setDescription(r.description());
        e.setCategory(r.category() == null || r.category().isBlank() ? "General" : r.category());
        e.setStartTime(r.startTime());
        e.setEndTime(r.endTime());
        int i = 0;
        for (String line : r.candidates()) {
            if (line == null || line.isBlank()) continue;
            String[] p = line.split("\\|");
            e.addCandidate(new Candidate(p[0].trim(), p.length > 1 ? p[1].trim() : "Independent",
                    EMOJIS[i++ % EMOJIS.length]));
        }
        if (e.getCandidates().size() < 2) throw new VotingException("At least 2 candidates are required");
        return elections.save(e);
    }

    @Transactional
    public Election close(Long id) {
        Election e = get(id);
        e.setClosed(true);
        return elections.save(e);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        votes.deleteByElectionId(id);
        elections.deleteById(id);
    }
}
