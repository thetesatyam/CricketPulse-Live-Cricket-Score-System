package com.cricketpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CorrectionRequest {
    @NotNull(message = "Delivery ID is required")
    private Long deliveryId;

    @NotBlank(message = "Reason for correction is required")
    private String reason;

    public CorrectionRequest() {}

    public CorrectionRequest(Long deliveryId, String reason) {
        this.deliveryId = deliveryId;
        this.reason = reason;
    }

    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
