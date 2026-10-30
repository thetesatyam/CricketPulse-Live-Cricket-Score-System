package com.cricketpulse.repository;

import com.cricketpulse.entity.PlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerMatchStatsRepository extends JpaRepository<PlayerMatchStats, Long> {
    List<PlayerMatchStats> findByMatchId(Long matchId);
    List<PlayerMatchStats> findByMatchIdAndTeamId(Long matchId, Long teamId);
    Optional<PlayerMatchStats> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    List<PlayerMatchStats> findByPlayerId(Long playerId);

    @Query("SELECT pms FROM PlayerMatchStats pms WHERE pms.match.id = :matchId AND pms.ballsFaced > 0 ORDER BY pms.runsScored DESC")
    List<PlayerMatchStats> findBattersByMatchId(Long matchId);

    @Query("SELECT pms FROM PlayerMatchStats pms WHERE pms.match.id = :matchId AND pms.legalBallsBowled > 0 ORDER BY pms.wicketsTaken DESC, pms.runsConceded ASC")
    List<PlayerMatchStats> findBowlersByMatchId(Long matchId);

    @Query("SELECT pms.player.id, pms.player.fullName, pms.team.shortName, SUM(pms.runsScored), SUM(pms.ballsFaced), SUM(pms.fours), SUM(pms.sixes) " +
           "FROM PlayerMatchStats pms GROUP BY pms.player.id, pms.player.fullName, pms.team.shortName ORDER BY SUM(pms.runsScored) DESC")
    List<Object[]> findTopRunScorersGlobal();

    @Query("SELECT pms.player.id, pms.player.fullName, pms.team.shortName, SUM(pms.wicketsTaken), SUM(pms.legalBallsBowled), SUM(pms.runsConceded) " +
           "FROM PlayerMatchStats pms GROUP BY pms.player.id, pms.player.fullName, pms.team.shortName ORDER BY SUM(pms.wicketsTaken) DESC")
    List<Object[]> findTopWicketTakersGlobal();
}
