package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.client.TeamServiceClient;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.repository.TournamentRepository;
import com.softdesign.tourney.service.TournamentQueryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentQueryServiceImpl implements TournamentQueryService {

    private final TournamentRepository tournamentRepository;
    private final TeamServiceClient teamServiceClient;

    @Autowired
    public TournamentQueryServiceImpl(TournamentRepository tournamentRepository,
                                      TeamServiceClient teamServiceClient) {
        this.tournamentRepository = tournamentRepository;
        this.teamServiceClient = teamServiceClient;
    }

    @Override
    @Transactional
    public List<TournamentDto> getTournaments() {
        return tournamentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TournamentDto findById(long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow();
        return mapToDto(tournament);
    }

    @Override
    public List<TournamentDto> searchTournaments(String query, String location) {
        return tournamentRepository.searchByQueryAndLocation(query, location)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TournamentDto mapToDto(Tournament tournament) {
        TournamentDto dto = new TournamentDto();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setLocation(tournament.getLocation());
        dto.setPrizeMoney(tournament.getPrizeMoney());
        dto.setVrsPoints(tournament.getVrsPoints());
        dto.setTeamIds(tournament.getTeamIds());

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