package com.cricketpulse.dto;

import com.cricketpulse.entity.Match;
import com.cricketpulse.entity.MatchStatus;
import com.cricketpulse.entity.MatchType;
import java.time.LocalDateTime;

public class MatchSummaryDto {
    private Long id;
    private String tournamentName;
    private String venueName;
    private String venueCity;
    private Long homeTeamId;
    private String homeTeamName;
    private String homeTeamShortName;
    private String homeTeamLogoUrl;
    private Long awayTeamId;
    private String awayTeamName;
    private String awayTeamShortName;
    private String awayTeamLogoUrl;
    private MatchType matchType;
    private int oversPerInnings;
    private LocalDateTime scheduledStartTime;
    private MatchStatus status;
    private int currentInningsNumber;
    private String result;
    private String winnerTeamShortName;

    // Current scores overview
    private String innings1Score; // e.g. "176/7 (20.0)"
    private String innings2Score; // e.g. "161/8 (20.0)"
    private String liveScore;     // current innings live line e.g. "AUS 28/0 (3.0)"
    private double currentRunRate;
    private Double requiredRunRate;

    public MatchSummaryDto() {}

    public static MatchSummaryDto fromEntity(Match m) {
        MatchSummaryDto dto = new MatchSummaryDto();
        dto.setId(m.getId());
        if (m.getTournament() != null) dto.setTournamentName(m.getTournament().getName());
        if (m.getVenue() != null) {
            dto.setVenueName(m.getVenue().getName());
            dto.setVenueCity(m.getVenue().getCity());
        }
        if (m.getHomeTeam() != null) {
            dto.setHomeTeamId(m.getHomeTeam().getId());
            dto.setHomeTeamName(m.getHomeTeam().getName());
            dto.setHomeTeamShortName(m.getHomeTeam().getShortName());
            dto.setHomeTeamLogoUrl(m.getHomeTeam().getLogoUrl());
        }
        if (m.getAwayTeam() != null) {
            dto.setAwayTeamId(m.getAwayTeam().getId());
            dto.setAwayTeamName(m.getAwayTeam().getName());
            dto.setAwayTeamShortName(m.getAwayTeam().getShortName());
            dto.setAwayTeamLogoUrl(m.getAwayTeam().getLogoUrl());
        }
        dto.setMatchType(m.getMatchType());
        dto.setOversPerInnings(m.getOversPerInnings());
        dto.setScheduledStartTime(m.getScheduledStartTime());
        dto.setStatus(m.getStatus());
        dto.setCurrentInningsNumber(m.getCurrentInningsNumber());
        dto.setResult(m.getResult());
        if (m.getWinnerTeam() != null) {
            dto.setWinnerTeamShortName(m.getWinnerTeam().getShortName());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueCity() { return venueCity; }
    public void setVenueCity(String venueCity) { this.venueCity = venueCity; }

    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }

    public String getHomeTeamShortName() { return homeTeamShortName; }
    public void setHomeTeamShortName(String homeTeamShortName) { this.homeTeamShortName = homeTeamShortName; }

    public String getHomeTeamLogoUrl() { return homeTeamLogoUrl; }
    public void setHomeTeamLogoUrl(String homeTeamLogoUrl) { this.homeTeamLogoUrl = homeTeamLogoUrl; }

    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }

    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }

    public String getAwayTeamShortName() { return awayTeamShortName; }
    public void setAwayTeamShortName(String awayTeamShortName) { this.awayTeamShortName = awayTeamShortName; }

    public String getAwayTeamLogoUrl() { return awayTeamLogoUrl; }
    public void setAwayTeamLogoUrl(String awayTeamLogoUrl) { this.awayTeamLogoUrl = awayTeamLogoUrl; }

    public MatchType getMatchType() { return matchType; }
    public void setMatchType(MatchType matchType) { this.matchType = matchType; }

    public int getOversPerInnings() { return oversPerInnings; }
    public void setOversPerInnings(int oversPerInnings) { this.oversPerInnings = oversPerInnings; }

    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public int getCurrentInningsNumber() { return currentInningsNumber; }
    public void setCurrentInningsNumber(int currentInningsNumber) { this.currentInningsNumber = currentInningsNumber; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getWinnerTeamShortName() { return winnerTeamShortName; }
    public void setWinnerTeamShortName(String winnerTeamShortName) { this.winnerTeamShortName = winnerTeamShortName; }

    public String getInnings1Score() { return innings1Score; }
    public void setInnings1Score(String innings1Score) { this.innings1Score = innings1Score; }

    public String getInnings2Score() { return innings2Score; }
    public void setInnings2Score(String innings2Score) { this.innings2Score = innings2Score; }

    public String getLiveScore() { return liveScore; }
    public void setLiveScore(String liveScore) { this.liveScore = liveScore; }

    public double getCurrentRunRate() { return currentRunRate; }
    public void setCurrentRunRate(double currentRunRate) { this.currentRunRate = currentRunRate; }

    public Double getRequiredRunRate() { return requiredRunRate; }
    public void setRequiredRunRate(Double requiredRunRate) { this.requiredRunRate = requiredRunRate; }
}
