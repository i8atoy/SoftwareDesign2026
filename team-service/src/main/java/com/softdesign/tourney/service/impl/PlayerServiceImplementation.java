package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.PlayerDto;
import com.softdesign.tourney.dto.TeamDto;
import com.softdesign.tourney.model.Player;
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
        PlayerDto playerDto = new PlayerDto();

        playerDto.setId(player.getId());
        playerDto.setAge(player.getAge());
        playerDto.setName(player.getName());
        playerDto.setPosition(player.getPosition());
        playerDto.setPhotoUrl(player.getPhotoUrl());


        if (player.getTeam() != null) {
            TeamDto tDto = new TeamDto();
            tDto.setId(player.getTeam().getId());
            tDto.setName(player.getTeam().getName());
            tDto.setCountry(player.getTeam().getCountry());
            tDto.setVrsPoints(player.getTeam().getVrsPoints());
            tDto.setPhotoUrl(player.getTeam().getPhotoUrl());

            playerDto.setTeam(tDto);
        }

        return playerDto;
    }
}