package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.model.Player;
import com.softdesign.tourney.model.Team;
import com.softdesign.tourney.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImplementation playerService;

    @Test
    void getPlayers_returnsAllMappedPlayers() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Natus Vincere");

        Player p1 = buildPlayer(1L, "s1mple", "Rifler", 26, team);
        Player p2 = buildPlayer(2L, "electroNic", "Rifler", 25, team);

        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of(p1, p2));

        List<PlayerDto> result = playerService.getPlayers();

        assertEquals(2, result.size());
        assertEquals("s1mple", result.get(0).getName());
        assertEquals("electroNic", result.get(1).getName());
    }

    @Test
    void getPlayers_returnsEmptyListWhenNone() {
        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of());

        List<PlayerDto> result = playerService.getPlayers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getPlayers_mapsAllFieldsCorrectly() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Natus Vincere");
        team.setCountry("Ukraine");
        team.setVrsPoints(1000);
        team.setPhotoUrl("https://example.com/navi.png");

        Player player = buildPlayer(1L, "s1mple", "Rifler", 26, team);
        player.setPhotoUrl("https://example.com/s1mple.png");

        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of(player));

        PlayerDto result = playerService.getPlayers().get(0);

        assertEquals(1L, result.getId());
        assertEquals("s1mple", result.getName());
        assertEquals("Rifler", result.getPosition());
        assertEquals(26, result.getAge());
        assertEquals("https://example.com/s1mple.png", result.getPhotoUrl());
        assertNotNull(result.getTeam());
        assertEquals("Natus Vincere", result.getTeam().getName());
    }

    @Test
    void getPlayers_handlesPlayerWithNoTeam() {
        Player player = buildPlayer(1L, "s1mple", "Rifler", 26, null);

        when(playerRepository.findAll(any(Sort.class))).thenReturn(List.of(player));

        List<PlayerDto> result = playerService.getPlayers();

        assertEquals(1, result.size());
        assertNull(result.get(0).getTeam());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

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