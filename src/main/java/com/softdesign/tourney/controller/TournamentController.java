package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.service.TournamentService;
import com.softdesign.tourney.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TournamentController {

    private final TournamentService tournamentService;
    private final UserService userService;

    @Autowired
    public TournamentController(TournamentService tournamentService, UserService userService) {
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

    @GetMapping("/tournaments")
    public String listTournaments(@RequestParam(required = false) String query, Model model, Principal principal) {

        List<TournamentDto> tournaments;

        if (query != null && !query.isBlank()) {
            tournaments = tournamentService.searchTournaments(query);
        } else {
            tournaments = tournamentService.getTournaments();
        }

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("query", query);

        if (principal != null) {
            UserEntity user = userService.findByUsername(principal.getName());
            if (user != null && user.getTeam() != null) {
                model.addAttribute("managerTeamId", user.getTeam().getId());
            }
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


}