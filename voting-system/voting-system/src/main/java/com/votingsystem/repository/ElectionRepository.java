package com.votingsystem.repository;

import com.votingsystem.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findAllByOrderByStartTimeDesc();
}
