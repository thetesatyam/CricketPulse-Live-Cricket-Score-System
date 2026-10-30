package com.cricketpulse.repository;

import com.cricketpulse.entity.Player;
import com.cricketpulse.entity.PlayingRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeamId(Long teamId);
    List<Player> findByPlayingRole(PlayingRole playingRole);
}
