package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.client.AuthServiceClient;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.publisher.TournamentEventPublisher;
import com.softdesign.tourney.repository.TournamentRepository;
import com.softdesign.tourney.service.TournamentCommandService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TournamentCommandServiceImpl implements TournamentCommandService {

    private final TournamentRepository tournamentRepository;
    private final AuthServiceClient authServiceClient;
    private final TournamentEventPublisher eventPublisher;

    @Autowired
    public TournamentCommandServiceImpl(TournamentRepository tournamentRepository,
                                        AuthServiceClient authServiceClient,
                                        TournamentEventPublisher eventPublisher) {
        this.tournamentRepository = tournamentRepository;
        this.authServiceClient    = authServiceClient;
        this.eventPublisher       = eventPublisher;
    }

    @Override
    public void saveTournament(TournamentDto dto) {
        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setLocation(dto.getLocation());
        tournament.setPrizeMoney(dto.getPrizeMoney());
        tournament.setVrsPoints(dto.getVrsPoints());
        tournament.setTeamIds(new ArrayList<>());
        tournamentRepository.save(tournament);

        eventPublisher.publishCreated(dto.getName(), currentUsername());
    }

    @Override
    @Transactional
    public void updateTournament(TournamentDto dto) {
        Tournament existing = tournamentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        existing.setName(dto.getName());
        existing.setLocation(dto.getLocation());
        existing.setPrizeMoney(dto.getPrizeMoney());
        existing.setVrsPoints(dto.getVrsPoints());
        tournamentRepository.save(existing);

        eventPublisher.publishUpdated(dto.getName(), currentUsername());
    }

    @Override
    public void deleteTournament(Long id) {
        // Fetch the name before deletion so we can include it in the event
        String name = tournamentRepository.findById(id)
                .map(Tournament::getName)
                .orElse("Unknown");

        tournamentRepository.deleteById(id);

        eventPublisher.publishDeleted(name, currentUsername());
    }

    @Override
    @Transactional
    public void joinTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament id: " + tournamentId));

        Long teamId = authServiceClient.getTeamIdForUser(username);
        if (teamId == null) {
            throw new IllegalStateException("User '" + username + "' has no managed team");
        }

        if (tournament.getTeamIds() == null) {
            tournament.setTeamIds(new ArrayList<>());
        }
        if (!tournament.getTeamIds().contains(teamId)) {
            tournament.getTeamIds().add(teamId);
            tournamentRepository.save(tournament);
        }
    }

    @Override
    @Transactional
    public void leaveTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament id: " + tournamentId));

        Long teamId = authServiceClient.getTeamIdForUser(username);
        if (teamId == null) return;

        if (tournament.getTeamIds() != null) {
            tournament.getTeamIds().remove(teamId);
            tournamentRepository.save(tournament);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";
    }
}