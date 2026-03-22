package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.models.Player;
import com.softdesign.tourney.repository.PlayerRepository;
import com.softdesign.tourney.service.PlayerService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImplementation implements PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerServiceImplementation(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<PlayerDto> getPlayers() {
        List<Player> players = playerRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return players.stream().map(this::mapToPlayerDto).collect(Collectors.toList());
    }

    private PlayerDto mapToPlayerDto(Player player) {
        PlayerDto playerDto = PlayerDto.builder()
                .id(player.getId())
                .age(player.getAge())
                .name(player.getName())
                .position(player.getPosition())
                .team(player.getTeam())
                .photoUrl(player.getPhotoUrl())
                .build();
        return playerDto;
    }
}
