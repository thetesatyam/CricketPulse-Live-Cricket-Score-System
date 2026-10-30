package com.cricketpulse.dto;

import com.cricketpulse.entity.Delivery;
import com.cricketpulse.entity.ExtraType;
import com.cricketpulse.entity.WicketType;
import java.time.LocalDateTime;

public class DeliveryResponse {
    private Long id;
    private Long inningsId;
    private int sequenceNumber;
    private int overNumber;
    private int ballNumber;
    private Long strikerId;
    private String strikerName;
    private Long nonStrikerId;
    private String nonStrikerName;
    private Long bowlerId;
    private String bowlerName;
    private int batterRuns;
    private ExtraType extraType;
    private int extraRuns;
    private boolean legalDelivery;
    private boolean wicket;
    private WicketType wicketType;
    private String dismissedPlayerName;
    private String commentary;
    private LocalDateTime createdAt;

    // Running match state snapshot
    private int totalRuns;
    private int wickets;
    private String oversFormatted;
    private double currentRunRate;
    private Long nextStrikerId;
    private Long nextNonStrikerId;
    private boolean isOverComplete;
    private boolean isInningsComplete;
    private boolean isMatchComplete;
    private String matchResult;

    public DeliveryResponse() {}

    public static DeliveryResponse fromEntity(Delivery d) {
        DeliveryResponse res = new DeliveryResponse();
        res.setId(d.getId());
        if (d.getInnings() != null) {
            res.setInningsId(d.getInnings().getId());
        }
        res.setSequenceNumber(d.getSequenceNumber());
        res.setOverNumber(d.getOverNumber());
        res.setBallNumber(d.getBallNumber());

        if (d.getStriker() != null) {
            res.setStrikerId(d.getStriker().getId());
            res.setStrikerName(d.getStriker().getFullName());
        }
        if (d.getNonStriker() != null) {
            res.setNonStrikerId(d.getNonStriker().getId());
            res.setNonStrikerName(d.getNonStriker().getFullName());
        }
        if (d.getBowler() != null) {
            res.setBowlerId(d.getBowler().getId());
            res.setBowlerName(d.getBowler().getFullName());
        }

        res.setBatterRuns(d.getBatterRuns());
        res.setExtraType(d.getExtraType());
        res.setExtraRuns(d.getExtraRuns());
        res.setLegalDelivery(d.isLegalDelivery());
        res.setWicket(d.isWicket());
        res.setWicketType(d.getWicketType());
        if (d.getDismissedPlayer() != null) {
            res.setDismissedPlayerName(d.getDismissedPlayer().getFullName());
        }
        res.setCommentary(d.getCommentary());
        res.setCreatedAt(d.getCreatedAt());
        return res;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInningsId() { return inningsId; }
    public void setInningsId(Long inningsId) { this.inningsId = inningsId; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }

    public Long getStrikerId() { return strikerId; }
    public void setStrikerId(Long strikerId) { this.strikerId = strikerId; }

    public String getStrikerName() { return strikerName; }
    public void setStrikerName(String strikerName) { this.strikerName = strikerName; }

    public Long getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(Long nonStrikerId) { this.nonStrikerId = nonStrikerId; }

    public String getNonStrikerName() { return nonStrikerName; }
    public void setNonStrikerName(String nonStrikerName) { this.nonStrikerName = nonStrikerName; }

    public Long getBowlerId() { return bowlerId; }
    public void setBowlerId(Long bowlerId) { this.bowlerId = bowlerId; }

    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }

    public int getBatterRuns() { return batterRuns; }
    public void setBatterRuns(int batterRuns) { this.batterRuns = batterRuns; }

    public ExtraType getExtraType() { return extraType; }
    public void setExtraType(ExtraType extraType) { this.extraType = extraType; }

    public int getExtraRuns() { return extraRuns; }
    public void setExtraRuns(int extraRuns) { this.extraRuns = extraRuns; }

    public boolean isLegalDelivery() { return legalDelivery; }
    public void setLegalDelivery(boolean legalDelivery) { this.legalDelivery = legalDelivery; }

    public boolean isWicket() { return wicket; }
    public void setWicket(boolean wicket) { this.wicket = wicket; }

    public WicketType getWicketType() { return wicketType; }
    public void setWicketType(WicketType wicketType) { this.wicketType = wicketType; }

    public String getDismissedPlayerName() { return dismissedPlayerName; }
    public void setDismissedPlayerName(String dismissedPlayerName) { this.dismissedPlayerName = dismissedPlayerName; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getTotalRuns() { return totalRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public String getOversFormatted() { return oversFormatted; }
    public void setOversFormatted(String oversFormatted) { this.oversFormatted = oversFormatted; }

    public double getCurrentRunRate() { return currentRunRate; }
    public void setCurrentRunRate(double currentRunRate) { this.currentRunRate = currentRunRate; }

    public Long getNextStrikerId() { return nextStrikerId; }
    public void setNextStrikerId(Long nextStrikerId) { this.nextStrikerId = nextStrikerId; }

    public Long getNextNonStrikerId() { return nextNonStrikerId; }
    public void setNextNonStrikerId(Long nextNonStrikerId) { this.nextNonStrikerId = nextNonStrikerId; }

    public boolean isOverComplete() { return isOverComplete; }
    public void setOverComplete(boolean overComplete) { isOverComplete = overComplete; }

    public boolean isInningsComplete() { return isInningsComplete; }
    public void setInningsComplete(boolean inningsComplete) { isInningsComplete = inningsComplete; }

    public boolean isMatchComplete() { return isMatchComplete; }
    public void setMatchComplete(boolean matchComplete) { isMatchComplete = matchComplete; }

    public String getMatchResult() { return matchResult; }
    public void setMatchResult(String matchResult) { this.matchResult = matchResult; }
}
