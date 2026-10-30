package com.cricketpulse.controller;

import com.cricketpulse.dto.*;
import com.cricketpulse.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches", description = "Match queries, scheduling, scorecards, and commentary")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    @Operation(summary = "List all matches")
    public ResponseEntity<ApiResponse<List<MatchSummaryDto>>> getAllMatches() {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getAllMatches()));
    }

    @GetMapping("/live")
    @Operation(summary = "List live and in-progress matches")
    public ResponseEntity<ApiResponse<List<MatchSummaryDto>>> getLiveMatches() {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getLiveMatches()));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "List upcoming scheduled matches")
    public ResponseEntity<ApiResponse<List<MatchSummaryDto>>> getUpcomingMatches() {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getUpcomingMatches()));
    }

    @GetMapping("/completed")
    @Operation(summary = "List completed matches")
    public ResponseEntity<ApiResponse<List<MatchSummaryDto>>> getCompletedMatches() {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getCompletedMatches()));
    }

    @GetMapping("/{matchId}")
    @Operation(summary = "Get detailed match view including live batsmen and bowler")
    public ResponseEntity<ApiResponse<MatchDetailDto>> getMatchDetails(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getMatchDetails(matchId)));
    }

    @GetMapping("/{matchId}/scorecard")
    @Operation(summary = "Get full official scorecard with batting and bowling breakdowns")
    public ResponseEntity<ApiResponse<ScorecardDto>> getScorecard(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getScorecard(matchId)));
    }

    @GetMapping("/{matchId}/commentary")
    @Operation(summary = "Get ball-by-ball commentary stream")
    public ResponseEntity<ApiResponse<List<CommentaryDto>>> getMatchCommentary(@PathVariable Long matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getMatchCommentary(matchId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Schedule a new cricket match (Admin only)")
    public ResponseEntity<ApiResponse<MatchSummaryDto>> createMatch(@Valid @RequestBody CreateMatchRequest request) {
        MatchSummaryDto created = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Match scheduled successfully", created));
    }

    @PostMapping("/{matchId}/start")
    @PreAuthorize("hasAnyRole('SCORER', 'ADMIN')")
    @Operation(summary = "Start match live")
    public ResponseEntity<ApiResponse<MatchSummaryDto>> startMatch(@PathVariable Long matchId) {
        MatchSummaryDto updated = matchService.startMatch(matchId);
        return ResponseEntity.ok(ApiResponse.ok("Match started live", updated));
    }
}
