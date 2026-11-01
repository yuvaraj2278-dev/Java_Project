package com.votingsystem.controller;

import com.votingsystem.dto.VoteReceipt;
import com.votingsystem.dto.VoteRequest;
import com.votingsystem.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService service;

    public VoteController(VoteService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<VoteReceipt> cast(@Valid @RequestBody VoteRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cast(r));
    }

    @GetMapping("/verify/{code}")
    public Map<String, Object> verify(@PathVariable String code) { return service.verify(code); }
}
