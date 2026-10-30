package com.cricketpulse.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchDetailDto {
    private MatchSummaryDto summary;

    // Current on-field live state
    private BatterScoreDto striker;
    private BatterScoreDto nonStriker;
    private BowlerScoreDto currentBowler;
    private List<String> recentBalls = new ArrayList<>(); // e.g. ["1", "0", "4", "W", "wd", "6"]

    private Integer target;
    private Integer runsNeeded;
    private Integer ballsRemaining;
    private Double requiredRunRate;

    private List<InningsDto> innings = new ArrayList<>();

    public MatchDetailDto() {}

    public MatchSummaryDto getSummary() { return summary; }
    public void setSummary(MatchSummaryDto summary) { this.summary = summary; }

    public BatterScoreDto getStriker() { return striker; }
    public void setStriker(BatterScoreDto striker) { this.striker = striker; }

    public BatterScoreDto getNonStriker() { return nonStriker; }
    public void setNonStriker(BatterScoreDto nonStriker) { this.nonStriker = nonStriker; }

    public BowlerScoreDto getCurrentBowler() { return currentBowler; }
    public void setCurrentBowler(BowlerScoreDto currentBowler) { this.currentBowler = currentBowler; }

    public List<String> getRecentBalls() { return recentBalls; }
    public void setRecentBalls(List<String> recentBalls) { this.recentBalls = recentBalls; }

    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }

    public Integer getRunsNeeded() { return runsNeeded; }
    public void setRunsNeeded(Integer runsNeeded) { this.runsNeeded = runsNeeded; }

    public Integer getBallsRemaining() { return ballsRemaining; }
    public void setBallsRemaining(Integer ballsRemaining) { this.ballsRemaining = ballsRemaining; }

    public Double getRequiredRunRate() { return requiredRunRate; }
    public void setRequiredRunRate(Double requiredRunRate) { this.requiredRunRate = requiredRunRate; }

    public List<InningsDto> getInnings() { return innings; }
    public void setInnings(List<InningsDto> innings) { this.innings = innings; }
}
