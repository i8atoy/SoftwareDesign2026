package com.softdesign.tourney.controller;

import com.softdesign.tourney.client.AuthServiceClient;
import com.softdesign.tourney.command.*;
import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.service.TournamentCommandService;
import com.softdesign.tourney.service.TournamentQueryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TournamentApiController {

    private final TournamentQueryService queryService;
    private final TournamentCommandService commandService;
    private final TournamentCommandExecutor commandExecutor;
    private final AuthServiceClient authServiceClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${team.service.url}")
    private String teamServiceUrl;

    @Autowired
    public TournamentApiController(TournamentQueryService queryService,
                                   TournamentCommandService commandService,
                                   TournamentCommandExecutor commandExecutor,
                                   AuthServiceClient authServiceClient) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.commandExecutor = commandExecutor;
        this.authServiceClient = authServiceClient;
    }

    // ── Tournaments ──────────────────────────────────────────────────────────────

    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentDto>> getTournaments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location) {

        List<TournamentDto> tournaments;
        if ((query != null && !query.isBlank()) || (location != null && !location.isBlank())) {
            tournaments = queryService.searchTournaments(query, location);
        } else {
            tournaments = queryService.getTournaments();
        }
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDto> getTournament(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/api/players")
    public ResponseEntity<List<PlayerDto>> getPlayers() {
        PlayerDto[] players = restTemplate
                .getForObject(teamServiceUrl + "/api/players", PlayerDto[].class);
        return ResponseEntity.ok(players != null ? Arrays.asList(players) : List.of());
    }

    @PostMapping("/tournaments")
    public ResponseEntity<TournamentDto> createTournament(
            @Valid @RequestBody TournamentDto dto) {
        commandExecutor.execute(new CreateTournamentCommand(commandService, dto));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<TournamentDto> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody TournamentDto dto) {
        dto.setId(id);
        commandExecutor.execute(new UpdateTournamentCommand(commandService, dto));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        commandExecutor.execute(new DeleteTournamentCommand(commandService, id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tournaments/{id}/join")
    public ResponseEntity<Void> joinTournament(@PathVariable Long id,
                                               Authentication auth) {
        commandExecutor.execute(new JoinTournamentCommand(commandService, id, auth.getName()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tournaments/{id}/leave")
    public ResponseEntity<Void> leaveTournament(@PathVariable Long id,
                                                Authentication auth) {
        commandExecutor.execute(new LeaveTournamentCommand(commandService, id, auth.getName()));
        return ResponseEntity.ok().build();
    }

    // ── Teams (proxied from team-service) ────────────────────────────────────────

    @GetMapping("/teams")
    public ResponseEntity<List<TeamDto>> getTeams() {
        TeamDto[] teams = restTemplate.getForObject(teamServiceUrl + "/api/teams", TeamDto[].class);
        return ResponseEntity.ok(teams != null ? Arrays.asList(teams) : List.of());
    }

    // ── Me (current user info) ───────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        Long teamId = authServiceClient.getTeamIdForUser(auth.getName());
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("username", auth.getName());
            put("roles", auth.getAuthorities().stream()
                    .map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toList()));
            put("managedTeamId", teamId);
        }});
    }
}
