package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.model.Player;
import com.softdesign.tourney.model.Team;
import com.softdesign.tourney.repository.PlayerRepository;
import com.softdesign.tourney.repository.TeamRepository;
import com.softdesign.tourney.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImplementation implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public TeamServiceImplementation(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<TeamDto> getAllTeams() {
        List<Team> teams = teamRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return teams.stream().map(this::mapToTeamDto).collect(Collectors.toList());
    }

    private TeamDto mapToTeamDto(Team team) {
        List<Player> players = playerRepository.findByTeamId(team.getId());

        List<PlayerDto> playerDtos = players.stream().map(player -> {
            PlayerDto pDto = new PlayerDto();
            pDto.setId(player.getId());
            pDto.setName(player.getName());
            pDto.setPosition(player.getPosition());
            pDto.setAge(player.getAge());
            pDto.setPhotoUrl(player.getPhotoUrl());
            return pDto;
        }).collect(Collectors.toList());

        TeamDto teamDto = new TeamDto();
        teamDto.setId(team.getId());
        teamDto.setName(team.getName());
        teamDto.setVrsPoints(team.getVrsPoints());
        teamDto.setCountry(team.getCountry());
        teamDto.setPhotoUrl(team.getPhotoUrl());
        teamDto.setPlayers(playerDtos);


        return teamDto;
    }
}