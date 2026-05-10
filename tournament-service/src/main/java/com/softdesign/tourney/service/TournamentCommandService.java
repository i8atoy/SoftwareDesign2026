package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.TournamentDto;

public interface TournamentCommandService {
    void saveTournament(TournamentDto dto);
    void updateTournament(TournamentDto dto);
    void deleteTournament(Long id);
    void joinTournament(Long tournamentId, String username);
    void leaveTournament(Long tournamentId, String username);
}