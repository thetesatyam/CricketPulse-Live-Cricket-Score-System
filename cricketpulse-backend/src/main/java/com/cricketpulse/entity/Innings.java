package com.cricketpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "innings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "innings_number"})
})
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Match match;

    @Column(name = "innings_number", nullable = false)
    private int inningsNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batting_team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team battingTeam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bowling_team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team bowlingTeam;

    @Column(name = "total_runs")
    private int totalRuns = 0;

    @Column(name = "wickets")
    private int wickets = 0;

    @Column(name = "legal_balls")
    private int legalBalls = 0;

    @Column(name = "extras")
    private int extras = 0;

    @Column(name = "wide_runs")
    private int wideRuns = 0;

    @Column(name = "no_ball_runs")
    private int noBallRuns = 0;

    @Column(name = "bye_runs")
    private int byeRuns = 0;

    @Column(name = "leg_bye_runs")
    private int legByeRuns = 0;

    @Column(name = "penalty_runs")
    private int penaltyRuns = 0;

    @Column(name = "target")
    private Integer target;

    @Column(name = "is_declared")
    private boolean isDeclared = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InningsStatus status = InningsStatus.IN_PROGRESS;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Innings() {}

    public String getOversFormatted() {
        int completedOvers = legalBalls / 6;
        int remainingBalls = legalBalls % 6;
        return completedOvers + "." + remainingBalls;
    }

    public double getRunRate() {
        if (legalBalls == 0) return 0.0;
        double oversDecimal = legalBalls / 6.0;
        return Math.round((totalRuns / oversDecimal) * 100.0) / 100.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public int getInningsNumber() { return inningsNumber; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }

    public Team getBattingTeam() { return battingTeam; }
    public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }

    public Team getBowlingTeam() { return bowlingTeam; }
    public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }

    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public int getLegalBalls() { return legalBalls; }
    public void setLegalBalls(int legalBalls) { this.legalBalls = legalBalls; }

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

    public int getPenaltyRuns() { return penaltyRuns; }
    public void setPenaltyRuns(int penaltyRuns) { this.penaltyRuns = penaltyRuns; }

    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }

    public boolean isDeclared() { return isDeclared; }
    public void setDeclared(boolean declared) { isDeclared = declared; }

    public InningsStatus getStatus() { return status; }
    public void setStatus(InningsStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
