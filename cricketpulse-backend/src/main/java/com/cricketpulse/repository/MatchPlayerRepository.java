package com.cricketpulse.repository;

import com.cricketpulse.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {
    List<MatchPlayer> findByMatchId(Long matchId);
    List<MatchPlayer> findByMatchIdAndTeamId(Long matchId, Long teamId);
    Optional<MatchPlayer> findByMatchIdAndPlayerId(Long matchId, Long playerId);
}
