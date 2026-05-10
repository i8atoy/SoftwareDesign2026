package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.TournamentDto;
import java.util.List;

public interface TournamentQueryService {
    List<TournamentDto> getTournaments();
    TournamentDto findById(long id);
    List<TournamentDto> searchTournaments(String query, String location);
}