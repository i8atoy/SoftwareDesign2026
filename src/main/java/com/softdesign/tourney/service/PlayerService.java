package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.PlayerDto;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface PlayerService {
    List<PlayerDto> getPlayers();

}
