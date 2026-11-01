package com.votingsystem.controller;

import com.votingsystem.dto.StatsResponse;
import com.votingsystem.service.VoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final VoteService service;

    public StatsController(VoteService service) { this.service = service; }

    @GetMapping
    public StatsResponse stats() { return service.stats(); }
}
