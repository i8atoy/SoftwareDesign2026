package com.softdesign.tourney.command;

import com.softdesign.tourney.service.TournamentCommandService;

public class DeleteTournamentCommand implements TournamentCommand {

    private final TournamentCommandService service;
    private final Long tournamentId;

    public DeleteTournamentCommand(TournamentCommandService service, Long tournamentId) {
        this.service = service;
        this.tournamentId = tournamentId;
    }

    @Override
    public void execute() {
        service.deleteTournament(tournamentId);
    }
}