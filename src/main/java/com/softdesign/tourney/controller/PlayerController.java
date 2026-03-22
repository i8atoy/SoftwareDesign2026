package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public String players(Model model) {
        List<PlayerDto> players = playerService.getPlayers();
        model.addAttribute("players", players);
        return "players-list";
    }

}
