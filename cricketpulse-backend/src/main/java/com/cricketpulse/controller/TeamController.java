package com.cricketpulse.controller;

import com.cricketpulse.dto.ApiResponse;
import com.cricketpulse.dto.CreateTeamRequest;
import com.cricketpulse.entity.Player;
import com.cricketpulse.entity.Team;
import com.cricketpulse.service.PlayerService;
import com.cricketpulse.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Team directory and management")
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;

    public TeamController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping
    @Operation(summary = "List all cricket teams")
    public ResponseEntity<ApiResponse<List<Team>>> getAllTeams() {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getAllTeams()));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Get team profile by ID")
    public ResponseEntity<ApiResponse<Team>> getTeamById(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getTeamById(teamId)));
    }

    @GetMapping("/{teamId}/players")
    @Operation(summary = "List players for a specific team squad")
    public ResponseEntity<ApiResponse<List<Player>>> getTeamPlayers(@PathVariable Long teamId) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayersByTeamId(teamId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new team (Admin only)")
    public ResponseEntity<ApiResponse<Team>> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Team created = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Team created successfully", created));
    }
}
