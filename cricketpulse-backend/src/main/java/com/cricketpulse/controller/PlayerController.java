package com.cricketpulse.controller;

import com.cricketpulse.dto.ApiResponse;
import com.cricketpulse.dto.CreatePlayerRequest;
import com.cricketpulse.dto.PlayerStatsDto;
import com.cricketpulse.entity.Player;
import com.cricketpulse.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@Tag(name = "Players", description = "Player profiles and historical performance statistics")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    @Operation(summary = "List all registered players")
    public ResponseEntity<ApiResponse<List<Player>>> getAllPlayers() {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getAllPlayers()));
    }

    @GetMapping("/{playerId}")
    @Operation(summary = "Get player profile details")
    public ResponseEntity<ApiResponse<Player>> getPlayerById(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerById(playerId)));
    }

    @GetMapping("/{playerId}/statistics")
    @Operation(summary = "Get player career tournament statistics")
    public ResponseEntity<ApiResponse<PlayerStatsDto>> getPlayerStatistics(@PathVariable Long playerId) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerStatistics(playerId)));
    }

    @GetMapping("/top-scorers")
    @Operation(summary = "Get tournament top run scorers leaderboard")
    public ResponseEntity<ApiResponse<List<PlayerStatsDto>>> getTopRunScorers() {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getTopRunScorers()));
    }

    @GetMapping("/top-wickets")
    @Operation(summary = "Get tournament top wicket takers leaderboard")
    public ResponseEntity<ApiResponse<List<PlayerStatsDto>>> getTopWicketTakers() {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getTopWicketTakers()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new player (Admin only)")
    public ResponseEntity<ApiResponse<Player>> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player created = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Player registered successfully", created));
    }
}
