package com.cricketpulse.controller;

import com.cricketpulse.dto.ApiResponse;
import com.cricketpulse.dto.CorrectionRequest;
import com.cricketpulse.dto.DeliveryRequest;
import com.cricketpulse.dto.DeliveryResponse;
import com.cricketpulse.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scoring")
@Tag(name = "Scoring Console", description = "Live ball-by-ball scoring and auditable correction endpoints")
@PreAuthorize("hasAnyRole('SCORER', 'ADMIN')")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/innings/{inningsId}/deliveries")
    @Operation(summary = "Record a ball-by-ball delivery in real time")
    public ResponseEntity<ApiResponse<DeliveryResponse>> recordDelivery(
            @PathVariable Long inningsId,
            @Valid @RequestBody DeliveryRequest request) {

        DeliveryResponse response = scoringService.recordDelivery(inningsId, request);
        return ResponseEntity.ok(ApiResponse.ok("Delivery recorded successfully", response));
    }

    @PostMapping("/deliveries/corrections")
    @Operation(summary = "Apply an auditable correction / reversal to a previous delivery")
    public ResponseEntity<ApiResponse<DeliveryResponse>> applyCorrection(
            @Valid @RequestBody CorrectionRequest request) {

        DeliveryResponse response = scoringService.applyCorrection(request);
        return ResponseEntity.ok(ApiResponse.ok("Delivery correction applied successfully", response));
    }
}
