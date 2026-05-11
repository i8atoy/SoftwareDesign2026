package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.client.TeamServiceClient;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentQueryServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TeamServiceClient teamServiceClient;

    @InjectMocks
    private TournamentQueryServiceImpl queryService;

    // ── getTournaments ────────────────────────────────────────────────────────

    @Test
    void getTournaments_returnsAllMappedTournaments() {
        Tournament t1 = buildTournament(1L, "IEM Cologne", "Cologne", 1_000_000, 2400, List.of());
        Tournament t2 = buildTournament(2L, "BLAST Premier", "Copenhagen", 500_000, 1000, List.of());

        when(tournamentRepository.findAll(any(Sort.class))).thenReturn(List.of(t1, t2));

        List<TournamentDto> result = queryService.getTournaments();

        assertEquals(2, result.size());
        assertEquals("IEM Cologne", result.get(0).getName());
        assertEquals("BLAST Premier", result.get(1).getName());
    }

    @Test
    void getTournaments_returnsEmptyListWhenNone() {
        when(tournamentRepository.findAll(any(Sort.class))).thenReturn(List.of());

        List<TournamentDto> result = queryService.getTournaments();

        assertTrue(result.isEmpty());
    }

    @Test
    void getTournaments_resolvesTeamsViaTeamServiceClient() {
        Tournament t = buildTournament(1L, "IEM Cologne", "Cologne", 1_000_000, 2400, List.of(10L, 11L));

        TeamDto team10 = new TeamDto();
        team10.setId(10L);
        team10.setName("Natus Vincere");

        TeamDto team11 = new TeamDto();
        team11.setId(11L);
        team11.setName("FaZe Clan");

        when(tournamentRepository.findAll(any(Sort.class))).thenReturn(List.of(t));
        when(teamServiceClient.getTeamById(10L)).thenReturn(team10);
        when(teamServiceClient.getTeamById(11L)).thenReturn(team11);

        List<TournamentDto> result = queryService.getTournaments();

        assertEquals(2, result.get(0).getTeams().size());
        assertEquals("Natus Vincere", result.get(0).getTeams().get(0).getName());
        assertEquals("FaZe Clan", result.get(0).getTeams().get(1).getName());
    }

    @Test
    void getTournaments_skipsNullTeamsFromClient() {
        Tournament t = buildTournament(1L, "IEM Cologne", "Cologne", 1_000_000, 2400, List.of(10L, 99L));

        when(tournamentRepository.findAll(any(Sort.class))).thenReturn(List.of(t));
        when(teamServiceClient.getTeamById(10L)).thenReturn(new TeamDto());
        when(teamServiceClient.getTeamById(99L)).thenReturn(null); // team not found

        List<TournamentDto> result = queryService.getTournaments();

        assertEquals(1, result.get(0).getTeams().size()); // null team skipped
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_returnsMappedDto() {
        Tournament t = buildTournament(1L, "IEM Katowice", "Katowice", 250_000, 1250, List.of());
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(t));

        TournamentDto result = queryService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("IEM Katowice", result.getName());
        assertEquals("Katowice", result.getLocation());
        assertEquals(250_000, result.getPrizeMoney());
        assertEquals(1250, result.getVrsPoints());
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> queryService.findById(99L));
    }

    // ── searchTournaments ─────────────────────────────────────────────────────

    @Test
    void searchTournaments_returnsMatchingResults() {
        Tournament t = buildTournament(1L, "IEM Cologne", "Cologne", 1_000_000, 2400, List.of());
        when(tournamentRepository.searchByQueryAndLocation("IEM", "Cologne"))
                .thenReturn(List.of(t));

        List<TournamentDto> result = queryService.searchTournaments("IEM", "Cologne");

        assertEquals(1, result.size());
        assertEquals("IEM Cologne", result.get(0).getName());
    }

    @Test
    void searchTournaments_returnsEmptyWhenNoMatch() {
        when(tournamentRepository.searchByQueryAndLocation("xyz", "nowhere"))
                .thenReturn(List.of());

        List<TournamentDto> result = queryService.searchTournaments("xyz", "nowhere");

        assertTrue(result.isEmpty());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Tournament buildTournament(Long id, String name, String location,
                                       double prize, int vrs, List<Long> teamIds) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName(name);
        t.setLocation(location);
        t.setPrizeMoney(prize);
        t.setVrsPoints(vrs);
        t.setTeamIds(teamIds);
        return t;
    }
}