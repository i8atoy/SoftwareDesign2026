package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.TeamDto;
import org.springframework.ui.Model;
import com.softdesign.tourney.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TeamController {
    private TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/teams")
    public String teams(Model model) {
        List<TeamDto> teams = teamService.getAllTeams();
        model.addAttribute("teams", teams);
        return "teams-list";
    }
}
