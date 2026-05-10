package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.client.AuthServiceClient;
import com.softdesign.tourney.client.TeamServiceClient;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.repository.TournamentRepository;
import com.softdesign.tourney.service.TournamentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImplementation implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final AuthServiceClient authServiceClient;
    private final TeamServiceClient teamServiceClient;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public TournamentServiceImplementation(TournamentRepository tournamentRepository,
                                           AuthServiceClient authServiceClient,
                                           TeamServiceClient teamServiceClient,
                                           ApplicationEventPublisher eventPublisher) {
        this.tournamentRepository = tournamentRepository;
        this.authServiceClient = authServiceClient;
        this.teamServiceClient = teamServiceClient;
        this.eventPublisher = eventPublisher;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "System";
    }

    // ── Read ────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<TournamentDto> getTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return tournaments.stream().map(this::mapToTournamentDto).collect(Collectors.toList());
    }

    @Override
    public TournamentDto findClubById(long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        return mapToTournamentDto(tournament);
    }

    @Override
    public List<TournamentDto> searchTournaments(String query, String location) {
        return tournamentRepository.searchByQueryAndLocation(query, location)
                .stream()
                .map(this::mapToTournamentDto)
                .collect(Collectors.toList());
    }

    // ── Write ───────────────────────────────────────────────────────────────────

    @Override
    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public void saveTournament(TournamentDto tournamentDto) {
        Tournament tournament = new Tournament();
        tournament.setName(tournamentDto.getName());
        tournament.setLocation(tournamentDto.getLocation());
        tournament.setPrizeMoney(tournamentDto.getPrizeMoney());
        tournament.setVrsPoints(tournamentDto.getVrsPoints());
        tournament.setTeamIds(new ArrayList<>());
        save(tournament);

        // TODO: publish ResourceEvent to RabbitMQ when message broker is added
    }

    @Override
    @Transactional
    public void updateTournament(TournamentDto tournamentDto) {
        Tournament existing = tournamentRepository.findById(tournamentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        existing.setName(tournamentDto.getName());
        existing.setLocation(tournamentDto.getLocation());
        existing.setPrizeMoney(tournamentDto.getPrizeMoney());
        existing.setVrsPoints(tournamentDto.getVrsPoints());
        tournamentRepository.save(existing);

        // TODO: publish ResourceEvent to RabbitMQ when message broker is added
    }

    @Override
    public void deleteTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament != null) {
            tournamentRepository.deleteById(tournamentId);
            // TODO: publish ResourceEvent to RabbitMQ when message broker is added
        }
    }

    // ── Join / Leave ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void joinTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament id: " + tournamentId));

        // Real REST call to auth-service — replaces the hardcoded stub
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
        // TODO: publish ResourceEvent to RabbitMQ when message broker is added
    }

    @Override
    @Transactional
    public void leaveTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament id: " + tournamentId));

        // Real REST call to auth-service — replaces the hardcoded stub
        Long teamId = authServiceClient.getTeamIdForUser(username);
        if (teamId == null) return;

        if (tournament.getTeamIds() != null) {
            tournament.getTeamIds().remove(teamId);
            tournamentRepository.save(tournament);
        }
        // TODO: publish ResourceEvent to RabbitMQ when message broker is added
    }

    // ── Mapping ─────────────────────────────────────────────────────────────────

    private TournamentDto mapToTournamentDto(Tournament tournament) {
        TournamentDto dto = new TournamentDto();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setLocation(tournament.getLocation());
        dto.setPrizeMoney(tournament.getPrizeMoney());
        dto.setVrsPoints(tournament.getVrsPoints());
        dto.setTeamIds(tournament.getTeamIds());

        // Resolve team IDs → TeamDto objects via team-service REST call
        List<TeamDto> teams = new ArrayList<>();
        if (tournament.getTeamIds() != null) {
            for (Long teamId : tournament.getTeamIds()) {
                TeamDto team = teamServiceClient.getTeamById(teamId);
                if (team != null) teams.add(team);
            }
        }
        dto.setTeams(teams);

        return dto;
    }
}
