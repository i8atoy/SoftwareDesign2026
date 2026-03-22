package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Team;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.repository.TournamentRepository;
import com.softdesign.tourney.repository.UserRepository;
import com.softdesign.tourney.service.TournamentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImplementation implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    @Autowired
    public TournamentServiceImplementation(TournamentRepository tournamentRepository, UserRepository userRepository) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
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

        save(tournament);
    }

    @Override
    public TournamentDto findClubById(long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
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
    }

    @Override
    public void deleteTournament(Long tournamentId) {
        tournamentRepository.deleteById(tournamentId);
    }

    private TournamentDto mapToTournamentDto(Tournament tournament) {
        List<TeamDto> teams = tournament.getTeams().stream().map(team -> TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .vrsPoints(team.getVrsPoints())
                .country(team.getCountry())
                .photoUrl(team.getPhotoUrl())
                .build()).collect(Collectors.toList());

        TournamentDto tournamentDto = TournamentDto.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .location(tournament.getLocation())
                .prizeMoney(tournament.getPrizeMoney())
                .vrsPoints(tournament.getVrsPoints())
                .teams(teams)
                .build();

        return tournamentDto;
    }

    @Override
    public List<TournamentDto> searchTournaments(String query) {
        return tournamentRepository
                .findByNameContainingIgnoreCase(query)
                .stream()
                .map(this::mapToTournamentDto)
                .toList();
    }

    @Override
    @Transactional
    public void joinTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament Id:" + tournamentId));

        UserEntity manager = userRepository.findUserByUserName(username);

        if (manager == null || manager.getTeam() == null) {
            throw new IllegalStateException("This manager does not have an assigned team.");
        }

        Team teamToJoin = manager.getTeam();

        if (!tournament.getTeams().contains(teamToJoin)) {
            tournament.getTeams().add(teamToJoin);
            tournamentRepository.save(tournament);
        }
    }

    @Override
    @Transactional
    public void leaveTournament(Long tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tournament Id:" + tournamentId));

        UserEntity manager = userRepository.findUserByUserName(username);

        if (manager != null && manager.getTeam() != null) {
            Team teamToLeave = manager.getTeam();

            tournament.getTeams().remove(teamToLeave);
            tournamentRepository.save(tournament);
        }
    }
}