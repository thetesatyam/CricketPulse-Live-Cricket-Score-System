package com.cricketpulse.controller;

import com.cricketpulse.dto.ApiResponse;
import com.cricketpulse.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@Tag(name = "Match Simulator", description = "Autonomous real-time cricket simulation controls")
@PreAuthorize("hasAnyRole('SCORER', 'ADMIN')")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/matches/{matchId}/start")
    @Operation(summary = "Start or resume match simulation loop")
    public ResponseEntity<ApiResponse<Map<String, Object>>> startSimulation(
            @PathVariable Long matchId,
            @RequestParam(required = false, defaultValue = "2000") Integer intervalMs) {

        boolean started = simulationService.startSimulation(matchId, intervalMs);
        return ResponseEntity.ok(ApiResponse.ok("Simulation started", Map.of("running", started, "matchId", matchId, "intervalMs", intervalMs)));
    }

    @PostMapping("/matches/{matchId}/pause")
    @Operation(summary = "Pause currently active match simulation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> pauseSimulation(@PathVariable Long matchId) {
        boolean paused = simulationService.pauseSimulation(matchId);
        return ResponseEntity.ok(ApiResponse.ok("Simulation paused", Map.of("running", !paused, "matchId", matchId)));
    }

    @PostMapping("/matches/{matchId}/stop")
    @Operation(summary = "Stop match simulation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stopSimulation(@PathVariable Long matchId) {
        boolean stopped = simulationService.stopSimulation(matchId);
        return ResponseEntity.ok(ApiResponse.ok("Simulation stopped", Map.of("running", false, "matchId", matchId)));
    }

    @GetMapping("/matches/{matchId}/status")
    @Operation(summary = "Check simulation running status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSimulationStatus(@PathVariable Long matchId) {
        boolean running = simulationService.isRunning(matchId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("matchId", matchId, "running", running)));
    }
}
