package com.votingsystem.config;

import com.votingsystem.model.*;
import com.votingsystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/** Seeds demo elections, voters (VT-DEMO01..05) and a few ballots on first launch. */
@Component
public class DataInitializer implements CommandLineRunner {
    private static final String[] EMOJIS = {"🦁", "🦅", "🐺", "🦊", "🐼", "🐯"};

    private final ElectionRepository elections;
    private final VoterRepository voters;
    private final VoteRepository votes;

    public DataInitializer(ElectionRepository elections, VoterRepository voters, VoteRepository votes) {
        this.elections = elections;
        this.voters = voters;
        this.votes = votes;
    }

    @Override
    public void run(String... args) {
        if (elections.count() > 0) return;
        LocalDateTime now = LocalDateTime.now();

        Election e1 = make("Student Council President 2026", "Choose the student who will lead the council this year.",
                "Student", now.minusDays(1), now.plusDays(7),
                "Aarav Sharma|Progress Alliance", "Meera Iyer|Unity Front", "Rohan Das|Independent");
        Election e2 = make("Favourite Programming Language", "Community poll: which language do you enjoy most?",
                "Poll", now.minusDays(2), now.plusDays(14),
                "Java|Backend", "Python|Data & AI", "JavaScript|Web", "Go|Cloud");
        Election e3 = make("Community Park Design", "Vote on how the new neighbourhood park should be built.",
                "Referendum", now.minusHours(5), now.plusDays(3),
                "Botanical Garden|Option A", "Sports Complex|Option B", "Open Amphitheatre|Option C");
        make("City Mayor 2027", "Upcoming city-wide mayoral election.",
                "Government", now.plusDays(30), now.plusDays(31),
                "Kavya Nair|Civic Front", "Arjun Menon|People's Party");

        for (int i = 1; i <= 5; i++) {
            Voter v = new Voter();
            v.setVoterId("VT-DEMO0" + i);
            v.setName("Demo Voter " + i);
            v.setEmail("demo" + i + "@example.com");
            voters.save(v);
        }
        // DEMO01 and DEMO02 are left free so you can try voting; 03-05 have already voted.
        for (int i = 3; i <= 5; i++) {
            ballot(e1, e1.getCandidates().get(i % 3), "VT-DEMO0" + i);
            ballot(e2, e2.getCandidates().get(i % 4), "VT-DEMO0" + i);
        }
        ballot(e3, e3.getCandidates().get(0), "VT-DEMO03");
        ballot(e3, e3.getCandidates().get(1), "VT-DEMO04");
    }

    private Election make(String title, String desc, String cat, LocalDateTime start, LocalDateTime end, String... cands) {
        Election e = new Election();
        e.setTitle(title); e.setDescription(desc); e.setCategory(cat);
        e.setStartTime(start); e.setEndTime(end);
        int i = 0;
        for (String c : cands) {
            String[] p = c.split("\\|");
            e.addCandidate(new Candidate(p[0], p[1], EMOJIS[i++ % EMOJIS.length]));
        }
        return elections.save(e);
    }

    private void ballot(Election e, Candidate c, String voterId) {
        Vote v = new Vote();
        v.setElectionId(e.getId());
        v.setCandidateId(c.getId());
        v.setVoterId(voterId);
        v.setReceiptCode("VR-SEED" + e.getId() + voterId.substring(voterId.length() - 2));
        votes.save(v);
    }
}
