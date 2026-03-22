package com.softdesign.tourney.repository;

import com.softdesign.tourney.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
     Player findByName(String name);
     List<Player> findByTeamId(Long teamId);
}
