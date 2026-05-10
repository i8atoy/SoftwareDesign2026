package com.softdesign.tourney.repository;

import com.softdesign.tourney.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByNameContainingIgnoreCase(String query);
    Optional<Tournament> findByName(String name);


    @Query("SELECT t FROM Tournament t WHERE " +
            "(:query IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:location IS NULL OR LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Tournament> searchByQueryAndLocation(@Param("query") String query, @Param("location") String location);
}
