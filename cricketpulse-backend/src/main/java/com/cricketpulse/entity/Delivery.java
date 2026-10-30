package com.cricketpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries", indexes = {
    @Index(name = "idx_delivery_innings", columnList = "innings_id"),
    @Index(name = "idx_delivery_sequence", columnList = "innings_id, sequence_number")
})
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "innings_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Innings innings;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "over_number", nullable = false)
    private int overNumber; // 0-indexed (e.g. 0 for 1st over, 19 for 20th over)

    @Column(name = "ball_number", nullable = false)
    private int ballNumber; // 1 to 6 (or beyond for extra balls)

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "striker_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player striker;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "non_striker_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player nonStriker;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bowler_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player bowler;

    @Column(name = "batter_runs")
    private int batterRuns = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "extra_type", length = 20)
    private ExtraType extraType = ExtraType.NONE;

    @Column(name = "extra_runs")
    private int extraRuns = 0;

    @Column(name = "is_legal_delivery")
    private boolean isLegalDelivery = true;

    @Column(name = "is_wicket")
    private boolean isWicket = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "wicket_type", length = 30)
    private WicketType wicketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissed_player_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player dismissedPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fielder_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Player fielder;

    @Column(length = 500)
    private String commentary;

    @Column(name = "is_reversed")
    private boolean isReversed = false;

    @Column(name = "reversal_reason")
    private String reversalReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Delivery() {}

    public int getTotalDeliveryRuns() {
        return batterRuns + extraRuns;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Innings getInnings() { return innings; }
    public void setInnings(Innings innings) { this.innings = innings; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }

    public Player getStriker() { return striker; }
    public void setStriker(Player striker) { this.striker = striker; }

    public Player getNonStriker() { return nonStriker; }
    public void setNonStriker(Player nonStriker) { this.nonStriker = nonStriker; }

    public Player getBowler() { return bowler; }
    public void setBowler(Player bowler) { this.bowler = bowler; }

    public int getBatterRuns() { return batterRuns; }
    public void setBatterRuns(int batterRuns) { this.batterRuns = batterRuns; }

    public ExtraType getExtraType() { return extraType; }
    public void setExtraType(ExtraType extraType) { this.extraType = extraType; }

    public int getExtraRuns() { return extraRuns; }
    public void setExtraRuns(int extraRuns) { this.extraRuns = extraRuns; }

    public boolean isLegalDelivery() { return isLegalDelivery; }
    public void setLegalDelivery(boolean legalDelivery) { isLegalDelivery = legalDelivery; }

    public boolean isWicket() { return isWicket; }
    public void setWicket(boolean wicket) { isWicket = wicket; }

    public WicketType getWicketType() { return wicketType; }
    public void setWicketType(WicketType wicketType) { this.wicketType = wicketType; }

    public Player getDismissedPlayer() { return dismissedPlayer; }
    public void setDismissedPlayer(Player dismissedPlayer) { this.dismissedPlayer = dismissedPlayer; }

    public Player getFielder() { return fielder; }
    public void setFielder(Player fielder) { this.fielder = fielder; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public boolean isReversed() { return isReversed; }
    public void setReversed(boolean reversed) { isReversed = reversed; }

    public String getReversalReason() { return reversalReason; }
    public void setReversalReason(String reversalReason) { this.reversalReason = reversalReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
