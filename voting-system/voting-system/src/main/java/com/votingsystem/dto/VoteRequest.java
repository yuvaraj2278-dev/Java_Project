package com.votingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(@NotBlank String voterId, @NotNull Long electionId, @NotNull Long candidateId) {}
