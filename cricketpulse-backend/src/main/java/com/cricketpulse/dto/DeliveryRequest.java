package com.cricketpulse.dto;

import com.cricketpulse.entity.ExtraType;
import com.cricketpulse.entity.WicketType;
import jakarta.validation.constraints.NotNull;

public class DeliveryRequest {

    @NotNull(message = "Striker ID is required")
    private Long strikerId;

    @NotNull(message = "Non-striker ID is required")
    private Long nonStrikerId;

    @NotNull(message = "Bowler ID is required")
    private Long bowlerId;

    private int batterRuns = 0;

    private ExtraType extraType = ExtraType.NONE;

    private int extraRuns = 0;

    private boolean isWicket = false;

    private WicketType wicketType;

    private Long dismissedPlayerId;

    private Long fielderId;

    private String commentary;

    private String idempotencyKey;

    public DeliveryRequest() {}

    // Getters and Setters
    public Long getStrikerId() { return strikerId; }
    public void setStrikerId(Long strikerId) { this.strikerId = strikerId; }

    public Long getNonStrikerId() { return nonStrikerId; }
    public void setNonStrikerId(Long nonStrikerId) { this.nonStrikerId = nonStrikerId; }

    public Long getBowlerId() { return bowlerId; }
    public void setBowlerId(Long bowlerId) { this.bowlerId = bowlerId; }

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

    public Long getDismissedPlayerId() { return dismissedPlayerId; }
    public void setDismissedPlayerId(Long dismissedPlayerId) { this.dismissedPlayerId = dismissedPlayerId; }

    public Long getFielderId() { return fielderId; }
    public void setFielderId(Long fielderId) { this.fielderId = fielderId; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
