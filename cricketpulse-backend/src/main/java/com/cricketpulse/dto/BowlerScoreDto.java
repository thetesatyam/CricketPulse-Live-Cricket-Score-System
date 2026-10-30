package com.cricketpulse.dto;

public class BowlerScoreDto {
    private Long playerId;
    private String playerName;
    private String overs;
    private int maidens;
    private int runs;
    private int wickets;
    private double economy;
    private int wides;
    private int noBalls;

    public BowlerScoreDto() {}

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getOvers() { return overs; }
    public void setOvers(String overs) { this.overs = overs; }

    public int getMaidens() { return maidens; }
    public void setMaidens(int maidens) { this.maidens = maidens; }

    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public double getEconomy() { return economy; }
    public void setEconomy(double economy) { this.economy = economy; }

    public int getWides() { return wides; }
    public void setWides(int wides) { this.wides = wides; }

    public int getNoBalls() { return noBalls; }
    public void setNoBalls(int noBalls) { this.noBalls = noBalls; }
}
