package com.cricketpulse.dto;

import com.cricketpulse.entity.PlayingRole;

public class PlayerStatsDto {
    private Long playerId;
    private String fullName;
    private String teamName;
    private String teamShortName;
    private PlayingRole playingRole;
    private int matchesPlayed;
    private int totalRuns;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private double battingAverage;
    private double strikeRate;
    private int wickets;
    private int legalBallsBowled;
    private int runsConceded;
    private double bowlingEconomy;

    public PlayerStatsDto() {}

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTeamShortName() { return teamShortName; }
    public void setTeamShortName(String teamShortName) { this.teamShortName = teamShortName; }

    public PlayingRole getPlayingRole() { return playingRole; }
    public void setPlayingRole(PlayingRole playingRole) { this.playingRole = playingRole; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }

    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }

    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }

    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }

    public double getBattingAverage() { return battingAverage; }
    public void setBattingAverage(double battingAverage) { this.battingAverage = battingAverage; }

    public double getStrikeRate() { return strikeRate; }
    public void setStrikeRate(double strikeRate) { this.strikeRate = strikeRate; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public int getLegalBallsBowled() { return legalBallsBowled; }
    public void setLegalBallsBowled(int legalBallsBowled) { this.legalBallsBowled = legalBallsBowled; }

    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }

    public double getBowlingEconomy() { return bowlingEconomy; }
    public void setBowlingEconomy(double bowlingEconomy) { this.bowlingEconomy = bowlingEconomy; }
}
