package com.cricketpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "match_players", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"match_id", "player_id"})
})
public class MatchPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player player;

    @Column(name = "is_captain")
    private boolean isCaptain = false;

    @Column(name = "is_wicket_keeper")
    private boolean isWicketKeeper = false;

    @Column(name = "batting_order")
    private int battingOrder = 0;

    public MatchPlayer() {}

    public MatchPlayer(Match match, Team team, Player player, boolean isCaptain, boolean isWicketKeeper, int battingOrder) {
        this.match = match;
        this.team = team;
        this.player = player;
        this.isCaptain = isCaptain;
        this.isWicketKeeper = isWicketKeeper;
        this.battingOrder = battingOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public boolean isCaptain() { return isCaptain; }
    public void setCaptain(boolean captain) { isCaptain = captain; }

    public boolean isWicketKeeper() { return isWicketKeeper; }
    public void setWicketKeeper(boolean wicketKeeper) { isWicketKeeper = wicketKeeper; }

    public int getBattingOrder() { return battingOrder; }
    public void setBattingOrder(int battingOrder) { this.battingOrder = battingOrder; }
}
