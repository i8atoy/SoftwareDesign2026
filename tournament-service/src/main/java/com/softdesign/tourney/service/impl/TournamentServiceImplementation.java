package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.repository.TournamentRepository;
import com.softdesign.tourney.service.TournamentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImplementation implements TournamentService {

    private final TournamentRepository tournamentRepository;


    @Autowired
    public TournamentServiceImplementation(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "System";
    }

    @Override
    @Transactional
    public List<TournamentDto> getTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return tournaments.stream().map(this::mapToTournamentDto).collect(Collectors.toList());
    }

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
        tournament.setTeamIds(new ArrayList<>()); // Initialize empty list

        save(tournament);

        // TODO: Send RabbitMQ Event -> new ResourceEvent("CREATED", "Tournament", tournament.getName(), getCurrentUsername())
    }

    @Override
    public TournamentDto findClubById(long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        return mapToTournamentDto(tournament);
    }

    @Override
    @Transactional
    public void updateTournament(TournamentDto tournamentDto) {
        Tournament existingTournament = tournamentRepository.findById(tournamentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));

        existingTournament.setName(tournamentDto.getName());
        existingTournament.setLocation(tournamentDto.getLocation());
        existingTournament.setPrizeMoney(tournamentDto.getPrizeMoney());
        existingTournament.setVrsPoints(tournamentDto.getVrsPoints());

        tournamentRepository.save(existingTournament);

        // TODO: Send RabbitMQ Event -> new ResourceEvent("UPDATED", "Tournament", existingTournament.getName(), getCurrentUsername())
    }

    @Override
    public void deleteTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament != null) {
            String name = tournament.getName();
            tournamentRepository.deleteById(tournamentId);

            // TODO: Send RabbitMQ Event -> new ResourceEvent("DELETED", "Tournament", name, getCurrentUsername())
        }
    }

    private TournamentDto mapToTournamentDto(Tournament tournament) {
        TournamentDto dto = new TournamentDto();

        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setLocation(tournament.getLocation());
        dto.setPrizeMoney(tournament.getPrizeMoney());
        dto.setVrsPoints(tournament.getVrsPoints());
        dto.setTeamIds(tournament.getTeamIds());

        return dto;
    }

    @Override
    public List<TournamentDto> searchTournaments(String query, String location) {
        List<Tournament> tournaments = tournamentRepository.searchByQueryAndLocation(query, location);

        return tournaments.stream()
                .map(this::mapToTournamentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament Id:" + tournamentId));

        // MICROSERVICE FIX: We cannot query the UserRepository here anymore.
        // TODO: Make a REST call to auth-service: GET http://localhost:8081/api/users/{username}/teamId
        Long teamId = 1L; // STUB: Hardcoded for now so it compiles

        if (tournament.getTeamIds() != null && !tournament.getTeamIds().contains(teamId)) {
            tournament.getTeamIds().add(teamId);
            tournamentRepository.save(tournament);

            // TODO: Send RabbitMQ Event ("JOINED")
        }
    }

    @Override
    @Transactional
    public void leaveTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament Id:" + tournamentId));

        // MICROSERVICE FIX: We cannot query the UserRepository here anymore.
        // TODO: Make a REST call to auth-service to get the team ID
        Long teamId = 1L; // STUB: Hardcoded for now so it compiles

        if (tournament.getTeamIds() != null) {
            tournament.getTeamIds().remove(teamId);
            tournamentRepository.save(tournament);

            // TODO: Send RabbitMQ Event ("LEFT")
        }
    }
}