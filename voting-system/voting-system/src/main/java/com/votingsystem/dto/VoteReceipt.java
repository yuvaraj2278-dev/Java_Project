package com.votingsystem.dto;

import java.time.LocalDateTime;

public record VoteReceipt(String receiptCode, String electionTitle, String candidateName, LocalDateTime castAt) {}
