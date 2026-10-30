package com.cricketpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_match_stats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "player_id"})
})
public class PlayerMatchStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Match match;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player player;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;

    // Batting stats
    @Column(name = "runs_scored")
    private int runsScored = 0;

    @Column(name = "balls_faced")
    private int ballsFaced = 0;

    @Column(name = "fours")
    private int fours = 0;

    @Column(name = "sixes")
    private int sixes = 0;

    @Column(name = "is_out")
    private boolean isOut = false;

    @Column(name = "dismissal_info", length = 100)
    private String dismissalInfo;

    @Column(name = "strike_rate")
    private double strikeRate = 0.0;

    // Bowling stats
    @Column(name = "legal_balls_bowled")
    private int legalBallsBowled = 0;

    @Column(name = "overs_bowled", length = 10)
    private String oversBowled = "0.0";

    @Column(name = "maiden_overs")
    private int maidenOvers = 0;

    @Column(name = "runs_conceded")
    private int runsConceded = 0;

    @Column(name = "wickets_taken")
    private int wicketsTaken = 0;

    @Column(name = "economy_rate")
    private double economyRate = 0.0;

    @Column(name = "wides_conceded")
    private int widesConceded = 0;

    @Column(name = "no_balls_conceded")
    private int noBallsConceded = 0;

    // Fielding stats
    @Column(name = "catches")
    private int catches = 0;

    @Column(name = "run_outs")
    private int runOuts = 0;

    @Column(name = "stumpings")
    private int stumpings = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
        // Recalculate strike rate
        if (ballsFaced > 0) {
            this.strikeRate = Math.round(((double) runsScored / ballsFaced) * 10000.0) / 100.0;
        } else {
            this.strikeRate = 0.0;
        }
        // Recalculate overs bowled string
        int completedOvers = legalBallsBowled / 6;
        int remainingBalls = legalBallsBowled % 6;
        this.oversBowled = completedOvers + "." + remainingBalls;
        // Recalculate economy rate
        if (legalBallsBowled > 0) {
            double oversDecimal = legalBallsBowled / 6.0;
            this.economyRate = Math.round((runsConceded / oversDecimal) * 100.0) / 100.0;
        } else {
            this.economyRate = 0.0;
        }
    }

    public PlayerMatchStats() {}

    public PlayerMatchStats(Match match, Player player, Team team) {
        this.match = match;
        this.player = player;
        this.team = team;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public int getRunsScored() { return runsScored; }
    public void setRunsScored(int runsScored) { this.runsScored = runsScored; }

    public int getBallsFaced() { return ballsFaced; }
    public void setBallsFaced(int ballsFaced) { this.ballsFaced = ballsFaced; }

    public int getFours() { return fours; }
    public void setFours(int fours) { this.fours = fours; }

    public int getSixes() { return sixes; }
    public void setSixes(int sixes) { this.sixes = sixes; }

    public boolean isOut() { return isOut; }
    public void setOut(boolean out) { isOut = out; }

    public String getDismissalInfo() { return dismissalInfo; }
    public void setDismissalInfo(String dismissalInfo) { this.dismissalInfo = dismissalInfo; }

    public double getStrikeRate() { return strikeRate; }
    public void setStrikeRate(double strikeRate) { this.strikeRate = strikeRate; }

    public int getLegalBallsBowled() { return legalBallsBowled; }
    public void setLegalBallsBowled(int legalBallsBowled) { this.legalBallsBowled = legalBallsBowled; }

    public String getOversBowled() { return oversBowled; }
    public void setOversBowled(String oversBowled) { this.oversBowled = oversBowled; }

    public int getMaidenOvers() { return maidenOvers; }
    public void setMaidenOvers(int maidenOvers) { this.maidenOvers = maidenOvers; }

    public int getRunsConceded() { return runsConceded; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }

    public int getWicketsTaken() { return wicketsTaken; }
    public void setWicketsTaken(int wicketsTaken) { this.wicketsTaken = wicketsTaken; }

    public double getEconomyRate() { return economyRate; }
    public void setEconomyRate(double economyRate) { this.economyRate = economyRate; }

    public int getWidesConceded() { return widesConceded; }
    public void setWidesConceded(int widesConceded) { this.widesConceded = widesConceded; }

    public int getNoBallsConceded() { return noBallsConceded; }
    public void setNoBallsConceded(int noBallsConceded) { this.noBallsConceded = noBallsConceded; }

    public int getCatches() { return catches; }
    public void setCatches(int catches) { this.catches = catches; }

    public int getRunOuts() { return runOuts; }
    public void setRunOuts(int runOuts) { this.runOuts = runOuts; }

    public int getStumpings() { return stumpings; }
    public void setStumpings(int stumpings) { this.stumpings = stumpings; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
