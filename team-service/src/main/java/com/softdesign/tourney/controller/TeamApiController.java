package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.service.PlayerService;
import com.softdesign.tourney.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamApiController {

    private final TeamService teamService;
    private final PlayerService playerService;

    @Autowired
    public TeamApiController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping("/api/teams")
    public List<TeamDto> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/api/teams/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/players")
    public List<PlayerDto> getAllPlayers() {
        return playerService.getPlayers();
    }
}