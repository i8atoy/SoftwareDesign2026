package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public interface TournamentService {
    List<TournamentDto> getTournaments();
    Tournament save(Tournament tournament);
    TournamentDto findClubById(long tournamentId);
    void saveTournament(TournamentDto tournamentDto);

    void updateTournament(TournamentDto tournamentDto);

    void deleteTournament(Long tournamentId);
    List<TournamentDto> searchTournaments(String query);
    void joinTournament(Long tournamentId, String username);
    void leaveTournament(Long tournamentId, String username);

}
