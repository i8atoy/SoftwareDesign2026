package com.softdesign.tourney.controller;

import com.softdesign.tourney.client.AuthServiceClient;
import com.softdesign.tourney.client.TeamServiceClient;
import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.service.TournamentCommandService;
import com.softdesign.tourney.service.TournamentQueryService;
import com.softdesign.tourney.strategy.CsvExportStrategy;
import com.softdesign.tourney.strategy.JsonExportStrategy;
import com.softdesign.tourney.strategy.XmlExportStrategy;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
public class TournamentController {

    private final AuthServiceClient authServiceClient;
    private final TournamentQueryService queryService;
    private final TournamentCommandService commandService;

    @Autowired
    public TournamentController(TournamentQueryService queryService,
                                TournamentCommandService commandService,
                                AuthServiceClient authServiceClient) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.authServiceClient = authServiceClient;
    }

    @Autowired
    private TeamServiceClient teamServiceClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${team.service.url}")
    private String teamServiceUrl;

    @GetMapping("/teams")
    public String teams(Model model) {
        com.softdesign.tourney.dto.TeamDto[] teams = restTemplate
                .getForObject(teamServiceUrl + "/api/teams", com.softdesign.tourney.dto.TeamDto[].class);
        model.addAttribute("teams", teams != null ? Arrays.asList(teams) : List.of());
        return "teams-list";
    }

    @GetMapping("/players")
    public String players(Model model) {
        PlayerDto[] players = restTemplate
                .getForObject(teamServiceUrl + "/api/players", PlayerDto[].class);
        model.addAttribute("players", players != null ? Arrays.asList(players) : List.of());
        return "players-list";
    }

    @GetMapping("/tournaments")
    public String listTournaments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            Model model, Principal principal) {

        List<TournamentDto> tournaments;
        if ((query != null && !query.isBlank()) || (location != null && !location.isBlank())) {
            tournaments = queryService.searchTournaments(query, location);
        } else {
            tournaments = queryService.getTournaments();
        }

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("query", query);
        model.addAttribute("location", location);

        if (principal != null) {
            model.addAttribute("username", principal.getName());
            Long managerTeamId = authServiceClient.getTeamIdForUser(principal.getName());
            model.addAttribute("managerTeamId", managerTeamId);
        }

        return "tournaments-list";
    }

    @GetMapping("/tournaments/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tournament", new TournamentDto());
        return "tournaments-create";
    }

    @PostMapping("/tournaments/new")
    public String saveTournament(@Valid @ModelAttribute("tournament") TournamentDto tournamentDto,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "tournaments-create";
        commandService.saveTournament(tournamentDto);
        return "redirect:/tournaments";
    }

    @GetMapping("/tournaments/{tournamentId}/edit")
    public String editTournamentForm(@PathVariable("tournamentId") int tournamentId, Model model) {
        model.addAttribute("tournament", queryService.findById(tournamentId));
        return "tournaments-edit";
    }

    @PostMapping("/tournaments/{tournamentId}/edit")
    public String editTournament(@PathVariable("tournamentId") Long tournamentId,
                                 @Valid @ModelAttribute("tournament") TournamentDto tournamentDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            tournamentDto.setId(tournamentId);
            model.addAttribute("tournament", tournamentDto);
            return "tournaments-edit";
        }
        tournamentDto.setId(tournamentId);
        commandService.updateTournament(tournamentDto);
        return "redirect:/tournaments";
    }

    @GetMapping("/tournaments/{tournamentId}/delete")
    public String deleteTournament(@PathVariable("tournamentId") Long tournamentId) {
        commandService.deleteTournament(tournamentId);
        return "redirect:/tournaments";
    }

    @PostMapping("/tournaments/{tournamentId}/join")
    public String joinTournament(@PathVariable("tournamentId") Long tournamentId, Principal principal) {
        commandService.joinTournament(tournamentId, principal.getName());
        return "redirect:/tournaments?joined";
    }

    @PostMapping("/tournaments/{tournamentId}/leave")
    public String leaveTournament(@PathVariable("tournamentId") Long tournamentId, Principal principal) {
        commandService.leaveTournament(tournamentId, principal.getName());
        return "redirect:/tournaments?left";
    }

    // ── Export ──────────────────────────────────────────────────────────────────

    @Autowired private JsonExportStrategy<TournamentDto> jsonStrategy;
    @Autowired private XmlExportStrategy<TournamentDto> xmlStrategy;
    @Autowired private CsvExportStrategy csvStrategy;

    @ResponseBody
    @GetMapping("/tournaments/export")
    public ResponseEntity<String> exportTournaments(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location) {

        List<TournamentDto> tournaments =
                ((query != null && !query.isBlank()) || (location != null && !location.isBlank()))
                        ? queryService.searchTournaments(query, location)
                        : queryService.getTournaments();

        String result;
        String contentType;
        String fileName = "tournaments";

        switch (format.toLowerCase()) {
            case "xml":
                result = xmlStrategy.export(tournaments);
                contentType = "application/xml";
                fileName += ".xml";
                break;
            case "csv":
                result = csvStrategy.export(tournaments);
                contentType = "text/csv";
                fileName += ".csv";
                break;
            default:
                result = jsonStrategy.export(tournaments);
                contentType = "application/json";
                fileName += ".json";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(result);
    }
}