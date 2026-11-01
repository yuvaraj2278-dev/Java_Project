package com.votingsystem.dto;

import java.util.List;

public record ResultsResponse(Long electionId, String title, String status, long totalVotes,
                              List<CandidateResult> results) {
    public record CandidateResult(Long candidateId, String name, String party, String emoji,
                                  long votes, double percentage) {}
}
