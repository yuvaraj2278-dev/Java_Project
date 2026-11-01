package com.votingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/** candidates: each entry is "Name | Party" (party optional). */
public record ElectionRequest(
        @NotBlank String title,
        String description,
        String category,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @Size(min = 2, message = "At least 2 candidates are required") List<String> candidates) {}
