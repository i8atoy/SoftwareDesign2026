package com.softdesign.tourney.command;

import com.softdesign.tourney.service.TournamentCommandService;

public class JoinTournamentCommand implements TournamentCommand {

    private final TournamentCommandService service;
    private final Long tournamentId;
    private final String username;

    public JoinTournamentCommand(TournamentCommandService service,
                                 Long tournamentId,
                                 String username) {
        this.service = service;
        this.tournamentId = tournamentId;
        this.username = username;
    }

    @Override
    public void execute() {
        service.joinTournament(tournamentId, username);
    }
}