package com.cricketpulse.dto;

import com.cricketpulse.entity.Delivery;
import com.cricketpulse.entity.ExtraType;
import com.cricketpulse.entity.WicketType;
import java.time.LocalDateTime;

public class CommentaryDto {
    private Long deliveryId;
    private int overNumber;
    private int ballNumber;
    private String overBallFormatted; // e.g., "14.2"
    private String bowlerName;
    private String strikerName;
    private int batterRuns;
    private ExtraType extraType;
    private int extraRuns;
    private boolean isWicket;
    private WicketType wicketType;
    private String dismissedPlayerName;
    private String commentary;
    private int totalRunsAfter;
    private int wicketsAfter;
    private LocalDateTime timestamp;

    public CommentaryDto() {}

    public static CommentaryDto fromDelivery(Delivery d) {
        CommentaryDto c = new CommentaryDto();
        c.setDeliveryId(d.getId());
        c.setOverNumber(d.getOverNumber());
        c.setBallNumber(d.getBallNumber());
        c.setOverBallFormatted(d.getOverNumber() + "." + d.getBallNumber());
        c.setBowlerName(d.getBowler().getFullName());
        c.setStrikerName(d.getStriker().getFullName());
        c.setBatterRuns(d.getBatterRuns());
        c.setExtraType(d.getExtraType());
        c.setExtraRuns(d.getExtraRuns());
        c.setWicket(d.isWicket());
        c.setWicketType(d.getWicketType());
        if (d.getDismissedPlayer() != null) {
            c.setDismissedPlayerName(d.getDismissedPlayer().getFullName());
        }
        c.setCommentary(d.getCommentary());
        c.setTimestamp(d.getCreatedAt());
        return c;
    }

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public int getOverNumber() { return overNumber; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }

    public int getBallNumber() { return ballNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }

    public String getOverBallFormatted() { return overBallFormatted; }
    public void setOverBallFormatted(String overBallFormatted) { this.overBallFormatted = overBallFormatted; }

    public String getBowlerName() { return bowlerName; }
    public void setBowlerName(String bowlerName) { this.bowlerName = bowlerName; }

    public String getStrikerName() { return strikerName; }
    public void setStrikerName(String strikerName) { this.strikerName = strikerName; }

    public int getBatterRuns() { return batterRuns; }
    public void setBatterRuns(int batterRuns) { this.batterRuns = batterRuns; }

    public ExtraType getExtraType() { return extraType; }
    public void setExtraType(ExtraType extraType) { this.extraType = extraType; }

    public int getExtraRuns() { return extraRuns; }
    public void setExtraRuns(int extraRuns) { this.extraRuns = extraRuns; }

    public boolean isWicket() { return isWicket; }
    public void setWicket(boolean wicket) { isWicket = wicket; }

    public WicketType getWicketType() { return wicketType; }
    public void setWicketType(WicketType wicketType) { this.wicketType = wicketType; }

    public String getDismissedPlayerName() { return dismissedPlayerName; }
    public void setDismissedPlayerName(String dismissedPlayerName) { this.dismissedPlayerName = dismissedPlayerName; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public int getTotalRunsAfter() { return totalRunsAfter; }
    public void setTotalRunsAfter(int totalRunsAfter) { this.totalRunsAfter = totalRunsAfter; }

    public int getWicketsAfter() { return wicketsAfter; }
    public void setWicketsAfter(int wicketsAfter) { this.wicketsAfter = wicketsAfter; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
