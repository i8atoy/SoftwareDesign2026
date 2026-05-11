package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.client.AuthServiceClient;
import com.softdesign.tourney.dto.TournamentDto;
import com.softdesign.tourney.models.Tournament;
import com.softdesign.tourney.publisher.TournamentEventPublisher;
import com.softdesign.tourney.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentCommandServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private TournamentEventPublisher eventPublisher;

    @InjectMocks
    private TournamentCommandServiceImpl commandService;

    @BeforeEach
    void setUpSecurityContext() {
        // Simulate a logged-in ADMIN user for SecurityContextHolder.getContext().getAuthentication()
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("admin", "password", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void saveTournament_persistsTournamentAndPublishesEvent() {
        TournamentDto dto = new TournamentDto();
        dto.setName("IEM Cologne");
        dto.setLocation("Cologne, Germany");
        dto.setPrizeMoney(1_000_000);
        dto.setVrsPoints(2400);

        when(tournamentRepository.save(any(Tournament.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        commandService.saveTournament(dto);

        verify(tournamentRepository, times(1)).save(any(Tournament.class));
        verify(eventPublisher, times(1)).publishCreated("IEM Cologne", "admin");
    }

    @Test
    void saveTournament_initializesEmptyTeamIds() {
        TournamentDto dto = new TournamentDto();
        dto.setName("BLAST Premier");
        dto.setLocation("Copenhagen");
        dto.setPrizeMoney(500_000);
        dto.setVrsPoints(1000);

        when(tournamentRepository.save(any(Tournament.class)))
                .thenAnswer(inv -> {
                    Tournament t = inv.getArgument(0);
                    assertNotNull(t.getTeamIds());
                    assertTrue(t.getTeamIds().isEmpty());
                    return t;
                });

        commandService.saveTournament(dto);
    }

    // ── updateTournament ──────────────────────────────────────────────────────

    @Test
    void updateTournament_updatesFieldsAndPublishesEvent() {
        Tournament existing = new Tournament();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setLocation("Old Location");
        existing.setPrizeMoney(100_000);
        existing.setVrsPoints(500);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tournamentRepository.save(any(Tournament.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TournamentDto dto = new TournamentDto();
        dto.setId(1L);
        dto.setName("New Name");
        dto.setLocation("New Location");
        dto.setPrizeMoney(200_000);
        dto.setVrsPoints(1000);

        commandService.updateTournament(dto);

        assertEquals("New Name", existing.getName());
        assertEquals("New Location", existing.getLocation());
        assertEquals(200_000, existing.getPrizeMoney());
        assertEquals(1000, existing.getVrsPoints());

        verify(eventPublisher, times(1)).publishUpdated("New Name", "admin");
    }

    @Test
    void updateTournament_throwsWhenNotFound() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        TournamentDto dto = new TournamentDto();
        dto.setId(99L);
        dto.setName("Ghost Tournament");
        dto.setLocation("Nowhere");

        assertThrows(IllegalArgumentException.class, () -> commandService.updateTournament(dto));
        verify(eventPublisher, never()).publishUpdated(any(), any());
    }

    // ── deleteTournament ──────────────────────────────────────────────────────

    @Test
    void deleteTournament_deletesAndPublishesEvent() {
        Tournament existing = new Tournament();
        existing.setId(1L);
        existing.setName("IEM Katowice");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(existing));

        commandService.deleteTournament(1L);

        verify(tournamentRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDeleted("IEM Katowice", "admin");
    }

    @Test
    void deleteTournament_publishesUnknownWhenNotFound() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        commandService.deleteTournament(99L);

        verify(tournamentRepository, times(1)).deleteById(99L);
        verify(eventPublisher, times(1)).publishDeleted("Unknown", "admin");
    }

    // ── joinTournament ────────────────────────────────────────────────────────

    @Test
    void joinTournament_addsTeamIdToTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTeamIds(new ArrayList<>());

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(authServiceClient.getTeamIdForUser("manager1")).thenReturn(42L);
        when(tournamentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        commandService.joinTournament(1L, "manager1");

        assertTrue(tournament.getTeamIds().contains(42L));
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void joinTournament_doesNotAddDuplicateTeam() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTeamIds(new ArrayList<>(List.of(42L)));

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(authServiceClient.getTeamIdForUser("manager1")).thenReturn(42L);

        commandService.joinTournament(1L, "manager1");

        assertEquals(1, tournament.getTeamIds().size());
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void joinTournament_throwsWhenUserHasNoTeam() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTeamIds(new ArrayList<>());

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(authServiceClient.getTeamIdForUser("manager1")).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> commandService.joinTournament(1L, "manager1"));
    }

    // ── leaveTournament ───────────────────────────────────────────────────────

    @Test
    void leaveTournament_removesTeamIdFromTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTeamIds(new ArrayList<>(List.of(42L)));

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(authServiceClient.getTeamIdForUser("manager1")).thenReturn(42L);
        when(tournamentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        commandService.leaveTournament(1L, "manager1");

        assertFalse(tournament.getTeamIds().contains(42L));
        verify(tournamentRepository).save(tournament);
    }
}