package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.model.Player;
import com.softdesign.tourney.model.Team;
import com.softdesign.tourney.repository.PlayerRepository;
import com.softdesign.tourney.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private TeamServiceImplementation teamService;

    // ── getAllTeams ────────────────────────────────────────────────────────────

    @Test
    void getAllTeams_returnsAllMappedTeams() {
        Team t1 = buildTeam(1L, "Natus Vincere", "Ukraine", 1000);
        Team t2 = buildTeam(2L, "FaZe Clan", "Europe", 900);

        when(teamRepository.findAll(any(Sort.class))).thenReturn(List.of(t1, t2));
        when(playerRepository.findByTeamId(any())).thenReturn(List.of());

        List<TeamDto> result = teamService.getAllTeams();

        assertEquals(2, result.size());
        assertEquals("Natus Vincere", result.get(0).getName());
        assertEquals("FaZe Clan", result.get(1).getName());
    }

    @Test
    void getAllTeams_returnsEmptyListWhenNone() {
        when(teamRepository.findAll(any(Sort.class))).thenReturn(List.of());

        List<TeamDto> result = teamService.getAllTeams();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTeams_includesPlayersForEachTeam() {
        Team team = buildTeam(1L, "Natus Vincere", "Ukraine", 1000);
        Player p1 = buildPlayer(1L, "s1mple", "Rifler", 26, team);
        Player p2 = buildPlayer(2L, "electroNic", "Rifler", 25, team);

        when(teamRepository.findAll(any(Sort.class))).thenReturn(List.of(team));
        when(playerRepository.findByTeamId(1L)).thenReturn(List.of(p1, p2));

        List<TeamDto> result = teamService.getAllTeams();

        assertEquals(2, result.get(0).getPlayers().size());
        assertEquals("s1mple", result.get(0).getPlayers().get(0).getName());
    }

    @Test
    void getAllTeams_mapsAllFieldsCorrectly() {
        Team team = buildTeam(1L, "Natus Vincere", "Ukraine", 1000);
        team.setPhotoUrl("https://example.com/navi.png");
        team.setManagerId(5L);

        when(teamRepository.findAll(any(Sort.class))).thenReturn(List.of(team));
        when(playerRepository.findByTeamId(1L)).thenReturn(List.of());

        TeamDto result = teamService.getAllTeams().get(0);

        assertEquals(1L, result.getId());
        assertEquals("Natus Vincere", result.getName());
        assertEquals("Ukraine", result.getCountry());
        assertEquals(1000, result.getVrsPoints());
        assertEquals("https://example.com/navi.png", result.getPhotoUrl());
        assertEquals(5L, result.getManagerId());
    }

    // ── getTeamById ───────────────────────────────────────────────────────────

    @Test
    void getTeamById_returnsMappedDto() {
        Team team = buildTeam(1L, "Natus Vincere", "Ukraine", 1000);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(playerRepository.findByTeamId(1L)).thenReturn(List.of());

        Optional<TeamDto> result = teamService.getTeamById(1L);

        assertTrue(result.isPresent());
        assertEquals("Natus Vincere", result.get().getName());
    }

    @Test
    void getTeamById_returnsEmptyWhenNotFound() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TeamDto> result = teamService.getTeamById(99L);

        assertTrue(result.isEmpty());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Team buildTeam(Long id, String name, String country, int vrsPoints) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        team.setCountry(country);
        team.setVrsPoints(vrsPoints);
        return team;
    }

    private Player buildPlayer(Long id, String name, String position, int age, Team team) {
        Player player = new Player();
        player.setId(id);
        player.setName(name);
        player.setPosition(position);
        player.setAge(age);
        player.setTeam(team);
        return player;
    }
}