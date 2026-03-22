package com.softdesign.tourney.repository;

import com.softdesign.tourney.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByNameContainingIgnoreCase(String query);
    Optional<Tournament> findByName(String name);
}
