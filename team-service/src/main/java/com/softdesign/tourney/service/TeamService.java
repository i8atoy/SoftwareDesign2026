package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.TeamDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TeamService {
    List<TeamDto> getAllTeams();
    Optional<TeamDto> getTeamById(Long id);
}
