package com.softdesign.tourney.command;

import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.service.TournamentCommandService;

public class CreateTournamentCommand implements TournamentCommand {

    private final TournamentCommandService service;
    private final TournamentDto dto;

    public CreateTournamentCommand(TournamentCommandService service, TournamentDto dto) {
        this.service = service;
        this.dto = dto;
    }

    @Override
    public void execute() {
        service.saveTournament(dto);
    }
}