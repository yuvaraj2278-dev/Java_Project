package com.votingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StatsResponse(long totalElections, long activeElections, long registeredVoters,
                            long totalVotes, List<RecentVote> recentVotes) {
    /** Voter IDs are masked in the audit log to protect ballot privacy. */
    public record RecentVote(String receiptCode, String electionTitle, String voter, LocalDateTime castAt) {}
}
