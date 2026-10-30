package com.cricketpulse.dto;

import com.cricketpulse.entity.InningsStatus;
import java.util.ArrayList;
import java.util.List;

public class InningsDto {
    private Long id;
    private int inningsNumber;
    private Long battingTeamId;
    private String battingTeamName;
    private String battingTeamShortName;
    private Long bowlingTeamId;
    private String bowlingTeamName;
    private String bowlingTeamShortName;
    private int totalRuns;
    private int wickets;
    private int legalBalls;
    private String oversFormatted;
    private double runRate;
    private int extras;
    private int wideRuns;
    private int noBallRuns;
    private int byeRuns;
    private int legByeRuns;
    private Integer target;
    private InningsStatus status;

    private List<BatterScoreDto> batters = new ArrayList<>();
    private List<BowlerScoreDto> bowlers = new ArrayList<>();

    public InningsDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }

    public Long getBattingTeamId() { return battingTeamId; }
    public void setBattingTeamId(Long battingTeamId) { this.battingTeamId = battingTeamId; }

    public String getBattingTeamName() { return battingTeamName; }
    public void setBattingTeamName(String battingTeamName) { this.battingTeamName = battingTeamName; }

    public String getBattingTeamShortName() { return battingTeamShortName; }
    public void setBattingTeamShortName(String battingTeamShortName) { this.battingTeamShortName = battingTeamShortName; }

    public Long getBowlingTeamId() { return bowlingTeamId; }
    public void setBowlingTeamId(Long bowlingTeamId) { this.bowlingTeamId = bowlingTeamId; }

    public String getBowlingTeamName() { return bowlingTeamName; }
    public void setBowlingTeamName(String bowlingTeamName) { this.bowlingTeamName = bowlingTeamName; }

    public String getBowlingTeamShortName() { return bowlingTeamShortName; }
    public void setBowlingTeamShortName(String bowlingTeamShortName) { this.bowlingTeamShortName = bowlingTeamShortName; }

    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public int getLegalBalls() { return legalBalls; }
    public void setLegalBalls(int legalBalls) { this.legalBalls = legalBalls; }

    public String getOversFormatted() { return oversFormatted; }
    public void setOversFormatted(String oversFormatted) { this.oversFormatted = oversFormatted; }

    public double getRunRate() { return runRate; }
    public void setRunRate(double runRate) { this.runRate = runRate; }

    public int getExtras() { return extras; }
    public void setExtras(int extras) { this.extras = extras; }

    public int getWideRuns() { return wideRuns; }
    public void setWideRuns(int wideRuns) { this.wideRuns = wideRuns; }

    public int getNoBallRuns() { return noBallRuns; }
    public void setNoBallRuns(int noBallRuns) { this.noBallRuns = noBallRuns; }

    public int getByeRuns() { return byeRuns; }
    public void setByeRuns(int byeRuns) { this.byeRuns = byeRuns; }

    public int getLegByeRuns() { return legByeRuns; }
    public void setLegByeRuns(int legByeRuns) { this.legByeRuns = legByeRuns; }

    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }

    public InningsStatus getStatus() { return status; }
    public void setStatus(InningsStatus status) { this.status = status; }

    public List<BatterScoreDto> getBatters() { return batters; }
    public void setBatters(List<BatterScoreDto> batters) { this.batters = batters; }

    public List<BowlerScoreDto> getBowlers() { return bowlers; }
    public void setBowlers(List<BowlerScoreDto> bowlers) { this.bowlers = bowlers; }
}
