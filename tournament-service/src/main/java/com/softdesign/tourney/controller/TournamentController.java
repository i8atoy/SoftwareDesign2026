package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.service.TournamentService;
import com.softdesign.tourney.strategy.CsvExportStrategy;
import com.softdesign.tourney.strategy.JsonExportStrategy;
import com.softdesign.tourney.strategy.XmlExportStrategy;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TournamentController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/tournaments")
    public String listTournaments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            Model model, Principal principal) {

        List<TournamentDto> tournaments;

        if ((query != null && !query.isBlank()) || (location != null && !location.isBlank())) {
            tournaments = tournamentService.searchTournaments(query, location);
        } else {
            tournaments = tournamentService.getTournaments();
        }

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("query", query);
        model.addAttribute("location", location);

        if (principal != null) {
            // MICROSERVICE FIX: We can no longer query the User database directly here.
            // Later, we will use RestTemplate or WebClient to ask the auth-service for this user's Team ID.
            // For now, we will just pass the username.
            model.addAttribute("username", principal.getName());
        }

        return "tournaments-list";
    }

    @GetMapping("/tournaments/new")
    public String showCreateForm(Model model) {
        TournamentDto tournament = new TournamentDto();
        model.addAttribute("tournament", tournament);
        return "tournaments-create";
    }

    @PostMapping("/tournaments/new")
    public String saveTournament(@Valid @ModelAttribute("tournament") TournamentDto tournamentDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "tournaments-create";
        }
        tournamentService.saveTournament(tournamentDto);
        return "redirect:/tournaments";
    }

    @GetMapping("/tournaments/{tournamentId}/edit")
    public String editTournamentForm(@PathVariable("tournamentId") int tournamentId, Model model) {
        TournamentDto tournamentDto = tournamentService.findClubById(tournamentId);
        model.addAttribute("tournament", tournamentDto);
        return "tournaments-edit";
    }

    @PostMapping("/tournaments/{tournamentId}/edit")
    public String editTournament(@PathVariable("tournamentId") Long tournamentId,
                                 @Valid @ModelAttribute("tournament") TournamentDto tournamentDto,
                                 BindingResult bindingResult,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            tournamentDto.setId(tournamentId);
            model.addAttribute("tournament", tournamentDto);
            return "tournaments-edit";
        }

        tournamentDto.setId(tournamentId);
        tournamentService.updateTournament(tournamentDto);

        return "redirect:/tournaments";
    }

    @GetMapping("/tournaments/{tournamentId}/delete")
    public String deleteTournament(@PathVariable("tournamentId") Long tournamentId) {
        tournamentService.deleteTournament(tournamentId);
        return "redirect:/tournaments";
    }

    @PostMapping("/tournaments/{tournamentId}/join")
    public String joinTournament(@PathVariable("tournamentId") Long tournamentId, Principal principal) {
        String username = principal.getName();
        tournamentService.joinTournament(tournamentId, username);
        return "redirect:/tournaments?joined";
    }

    @PostMapping("/tournaments/{tournamentId}/leave")
    public String leaveTournament(@PathVariable("tournamentId") Long tournamentId, Principal principal) {
        String username = principal.getName();
        tournamentService.leaveTournament(tournamentId, username);
        return "redirect:/tournaments?left";
    }

    @Autowired
    private JsonExportStrategy<TournamentDto> jsonStrategy;
    @Autowired
    private XmlExportStrategy<TournamentDto> xmlStrategy;
    @Autowired
    private CsvExportStrategy csvStrategy;

    @ResponseBody
    @GetMapping("/tournaments/export")
    public ResponseEntity<String> exportTournaments(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location) {

        List<TournamentDto> tournaments;

        if ((query != null && !query.isBlank()) || (location != null && !location.isBlank())) {
            tournaments = tournamentService.searchTournaments(query, location);
        } else {
            tournaments = tournamentService.getTournaments();
        }

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
            case "json":
            default:
                result = jsonStrategy.export(tournaments);
                contentType = "application/json";
                fileName += ".json";
                break;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(result);
    }
}