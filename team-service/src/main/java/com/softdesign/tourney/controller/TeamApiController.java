package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal REST API consumed by tournament-service.
 * Not exposed to the browser directly.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamApiController {

    private final TeamService teamService;

    @Autowired
    public TeamApiController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamDto> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
