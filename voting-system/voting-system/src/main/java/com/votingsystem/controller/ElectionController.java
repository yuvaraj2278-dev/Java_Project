package com.votingsystem.controller;

import com.votingsystem.dto.ElectionRequest;
import com.votingsystem.dto.ResultsResponse;
import com.votingsystem.model.Election;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {
    private final ElectionService service;
    private final VoteService voteService;

    public ElectionController(ElectionService service, VoteService voteService) {
        this.service = service;
        this.voteService = voteService;
    }

    @GetMapping
    public List<Election> list(@RequestParam(required = false) String category,
                               @RequestParam(required = false) String search) {
        return service.findAll(category, search);
    }

    @GetMapping("/{id}")
    public Election get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/{id}/results")
    public ResultsResponse results(@PathVariable Long id) { return voteService.results(id); }

    @PostMapping
    public ResponseEntity<Election> create(@Valid @RequestBody ElectionRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @PutMapping("/{id}/close")
    public Election close(@PathVariable Long id) { return service.close(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
