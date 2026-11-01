# 🗳️ VoteSecure - Online Voting System

A full-stack Voting System built with **Java (Spring Boot)**, **MySQL (XAMPP / phpMyAdmin)** and modern **HTML5, CSS3 and vanilla JavaScript**.

## 🌟 Features

**Frontend (HTML5 / CSS3 / Vanilla JS)**
- **Elections Showcase:** Browse student polls, referendums and government elections with status badges (Upcoming / Active / Closed).
- **Secure Ballot Modal:** Pick one candidate, enter your Voter ID, cast your vote once.
- **Digital Vote Receipt:** Unique receipt code (e.g. `VR-7K2M9QXD`) with print support.
- **Live Results:** Animated bar charts with percentages, auto-refresh every 5 seconds.
- **Voter Registration:** Get a unique Voter ID (e.g. `VT-4HF92K`); re-registering the same email returns the existing ID.
- **Receipt Verification:** Confirm a ballot was recorded - without revealing who it was cast for.
- **Admin Dashboard:** Stats, create / close / delete elections, and a privacy-masked audit log.

**Backend (Java / Spring Boot)**
- RESTful API for Elections, Voters, Votes, Results and Stats.
- **Double-vote prevention:** service-level check *and* a DB unique constraint on `(election_id, voter_id)`.
- Elections only accept votes while `ACTIVE` (status computed from start/end time or manual close).
- Auto table creation and demo data seeding (`DataInitializer.java`).

**Database (XAMPP / MySQL)**
- Connects to XAMPP MySQL on port 3306; auto-creates `voting_db`.
- Standalone `schema.sql` included for manual import via phpMyAdmin.

## 🚀 Quick Start

### Step 1: Start XAMPP
Start **MySQL** in the XAMPP Control Panel (default `localhost:3306`, user `root`, blank password).
Optional: view tables at http://localhost/phpmyadmin.

### Step 2: Open and Run in IntelliJ IDEA
1. **File → Open…** and select the `voting-system` folder.
2. IntelliJ detects `pom.xml` and syncs Maven dependencies.
3. Make sure **Lombok** annotation processing is on: *Settings → Build → Compiler → Annotation Processors → Enable*.
4. Select the **VotingApplication** run configuration and click ▶️ (or run `VotingApplication.java`).

### Step 3: Open the Web App
http://localhost:8080

### 🧪 Demo Voter IDs
| Voter ID | State |
|---|---|
| `VT-DEMO01`, `VT-DEMO02` | Haven't voted yet - use these to try voting |
| `VT-DEMO03` - `VT-DEMO05` | Already voted (try voting again to see the block) |

## 📁 Project Architecture
```
voting-system/
├── .run/VotingApplication.run.xml      <-- IntelliJ Run button
├── pom.xml                             <-- Spring Boot 3.2.5, Java 17
├── schema.sql                          <-- SQL script for phpMyAdmin
├── README.md
└── src/main/
    ├── java/com/votingsystem/
    │   ├── VotingApplication.java
    │   ├── config/        CorsConfig, DataInitializer
    │   ├── controller/    ElectionController, VoterController, VoteController, StatsController
    │   ├── dto/           VoteRequest, VoterRegistration, ElectionRequest, VoteReceipt, ResultsResponse, StatsResponse
    │   ├── exception/     GlobalExceptionHandler, ResourceNotFoundException, VotingException, AlreadyVotedException
    │   ├── model/         Election, Candidate, Voter, Vote
    │   ├── repository/    ElectionRepository, VoterRepository, VoteRepository
    │   └── service/       ElectionService, VoterService, VoteService
    └── resources/
        ├── application.properties      <-- XAMPP MySQL connection
        └── static/ (index.html, css/style.css, js/app.js)
```

## 🛠️ API Reference
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/elections` | List elections (`?category=Poll&search=Java`) |
| GET | `/api/elections/{id}` | Election details with candidates |
| GET | `/api/elections/{id}/results` | Vote counts and percentages |
| POST | `/api/elections` | Create election (Admin) |
| PUT | `/api/elections/{id}/close` | Close voting early |
| DELETE | `/api/elections/{id}` | Delete election and its votes |
| POST | `/api/voters/register` | Register and receive a Voter ID |
| GET | `/api/voters/{voterId}` | Look up a voter |
| GET | `/api/voters/{voterId}/voted` | Election IDs this voter has voted in |
| POST | `/api/votes` | Cast a vote `{voterId, electionId, candidateId}` |
| GET | `/api/votes/verify/{code}` | Verify a receipt code |
| GET | `/api/stats` | Dashboard metrics and masked audit log |

## ⚠️ Notes
This is a learning project. Production voting needs authentication (OTP / SSO), admin login, HTTPS, and cryptographic ballot anonymity.
