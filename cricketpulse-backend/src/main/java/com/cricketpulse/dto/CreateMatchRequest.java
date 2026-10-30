package com.cricketpulse.dto;

import com.cricketpulse.entity.MatchType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateMatchRequest {
    private Long tournamentId;
    private Long venueId;

    @NotNull(message = "Home team is required")
    private Long homeTeamId;

    @NotNull(message = "Away team is required")
    private Long awayTeamId;

    @NotNull(message = "Match type is required")
    private MatchType matchType = MatchType.T20;

    private int oversPerInnings = 20;

    @NotNull(message = "Scheduled start time is required")
    private LocalDateTime scheduledStartTime;

    public CreateMatchRequest() {}

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }

    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }

    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }

    public MatchType getMatchType() { return matchType; }
    public void setMatchType(MatchType matchType) { this.matchType = matchType; }

    public int getOversPerInnings() { return oversPerInnings; }
    public void setOversPerInnings(int oversPerInnings) { this.oversPerInnings = oversPerInnings; }

    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }
}
