package com.cricketpulse.repository;

import com.cricketpulse.entity.Match;
import com.cricketpulse.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByStatusIn(List<MatchStatus> statuses);
    Optional<Match> findByExternalProviderId(String externalProviderId);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId ORDER BY m.scheduledStartTime DESC")
    List<Match> findByTeamId(Long teamId);

    List<Match> findAllByOrderByScheduledStartTimeDesc();
}
