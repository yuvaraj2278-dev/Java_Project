package com.votingsystem.controller;

import com.votingsystem.dto.VoterRegistration;
import com.votingsystem.model.Voter;
import com.votingsystem.service.VoterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voters")
public class VoterController {
    private final VoterService service;

    public VoterController(VoterService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<Voter> register(@Valid @RequestBody VoterRegistration r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(r));
    }

    @GetMapping("/{voterId}")
    public Voter get(@PathVariable String voterId) { return service.get(voterId); }

    @GetMapping("/{voterId}/voted")
    public List<Long> voted(@PathVariable String voterId) { return service.votedElections(voterId); }
}
