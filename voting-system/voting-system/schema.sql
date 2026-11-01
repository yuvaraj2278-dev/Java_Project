-- Optional: manual import via phpMyAdmin (the app also auto-creates these tables).
CREATE DATABASE IF NOT EXISTS voting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE voting_db;

CREATE TABLE IF NOT EXISTS elections (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    category    VARCHAR(255),
    start_time  DATETIME(6) NOT NULL,
    end_time    DATETIME(6) NOT NULL,
    closed      BIT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS candidates (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    party       VARCHAR(255),
    emoji       VARCHAR(255),
    election_id BIGINT,
    FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS voters (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    voter_id      VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    registered_at DATETIME(6)
);

CREATE TABLE IF NOT EXISTS votes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id  BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    voter_id     VARCHAR(255) NOT NULL,
    receipt_code VARCHAR(255) NOT NULL UNIQUE,
    cast_at      DATETIME(6),
    CONSTRAINT uk_one_vote UNIQUE (election_id, voter_id)   -- one vote per voter per election
);
