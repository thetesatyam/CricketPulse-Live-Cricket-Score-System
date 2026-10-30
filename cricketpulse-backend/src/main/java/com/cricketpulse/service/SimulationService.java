package com.cricketpulse.service;

import com.cricketpulse.dto.DeliveryRequest;
import com.cricketpulse.dto.DeliveryResponse;
import com.cricketpulse.entity.*;
import com.cricketpulse.exception.BadRequestException;
import com.cricketpulse.exception.ResourceNotFoundException;
import com.cricketpulse.repository.*;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class SimulationService {

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final ScoringService scoringService;
    private final AuditService auditService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> activeSimulations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> simulationSpeeds = new ConcurrentHashMap<>();

    private final Random random = new Random();

    @Value("${cricketpulse.simulation.default-interval-ms:2000}")
    private int defaultIntervalMs;

    public SimulationService(MatchRepository matchRepository,
                             InningsRepository inningsRepository,
                             PlayerRepository playerRepository,
                             MatchPlayerRepository matchPlayerRepository,
                             ScoringService scoringService,
                             AuditService auditService) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.playerRepository = playerRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.scoringService = scoringService;
        this.auditService = auditService;
    }

    public synchronized boolean startSimulation(Long matchId, Integer intervalMs) {
        if (activeSimulations.containsKey(matchId) && !activeSimulations.get(matchId).isDone()) {
            throw new BadRequestException("Simulation is already running for match ID: " + matchId);
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new BadRequestException("Cannot simulate a completed match");
        }

        if (match.getStatus() == MatchStatus.SCHEDULED) {
            match.setStatus(MatchStatus.LIVE);
            matchRepository.save(match);
        }

        int speed = (intervalMs != null && intervalMs >= 200) ? intervalMs : defaultIntervalMs;
        simulationSpeeds.put(matchId, speed);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
                () -> tickSimulation(matchId),
                500,
                speed,
                TimeUnit.MILLISECONDS
        );

        activeSimulations.put(matchId, future);
        auditService.log("SIMULATION_START", "Match", matchId, "Simulation started with interval " + speed + "ms");
        return true;
    }

    public synchronized boolean pauseSimulation(Long matchId) {
        ScheduledFuture<?> future = activeSimulations.remove(matchId);
        if (future != null) {
            future.cancel(false);
            auditService.log("SIMULATION_PAUSE", "Match", matchId, "Simulation paused");
            return true;
        }
        return false;
    }

    public synchronized boolean stopSimulation(Long matchId) {
        ScheduledFuture<?> future = activeSimulations.remove(matchId);
        simulationSpeeds.remove(matchId);
        if (future != null) {
            future.cancel(false);
            auditService.log("SIMULATION_STOP", "Match", matchId, "Simulation stopped");
            return true;
        }
        return false;
    }

    public boolean isRunning(Long matchId) {
        ScheduledFuture<?> future = activeSimulations.get(matchId);
        return future != null && !future.isDone();
    }

    public void tickSimulation(Long matchId) {
        try {
            Match match = matchRepository.findById(matchId).orElse(null);
            if (match == null || match.getStatus() == MatchStatus.COMPLETED) {
                stopSimulation(matchId);
                return;
            }

            List<Innings> inningsList = inningsRepository.findByMatchIdOrderByInningsNumberAsc(matchId);
            if (inningsList.isEmpty()) {
                stopSimulation(matchId);
                return;
            }

            Innings currentInnings = inningsList.get(inningsList.size() - 1);
            if (currentInnings.getStatus() == InningsStatus.COMPLETED) {
                if (match.getCurrentInningsNumber() == 1) {
                    // Waiting for 2nd innings to start or next loop
                    return;
                } else {
                    stopSimulation(matchId);
                    return;
                }
            }

            // Get team squads
            List<Player> battingSquad = playerRepository.findByTeamId(currentInnings.getBattingTeam().getId());
            List<Player> bowlingSquad = playerRepository.findByTeamId(currentInnings.getBowlingTeam().getId());

            if (battingSquad.size() < 2 || bowlingSquad.isEmpty()) {
                stopSimulation(matchId);
                return;
            }

            // Pick active striker and non-striker
            int wicketsFallen = currentInnings.getWickets();
            int strikerIdx = Math.min(wicketsFallen, battingSquad.size() - 2);
            int nonStrikerIdx = Math.min(wicketsFallen + 1, battingSquad.size() - 1);

            Player striker = battingSquad.get(strikerIdx);
            Player nonStriker = battingSquad.get(nonStrikerIdx);

            // Cycle bowlers (e.g. bowlers 7..10 in squad)
            int over = currentInnings.getLegalBalls() / 6;
            int bowlerIdx = Math.max(0, bowlingSquad.size() - 1 - (over % 5));
            Player bowler = bowlingSquad.get(bowlerIdx);

            // Generate plausible delivery outcome
            DeliveryRequest req = generatePlausibleDelivery(striker, nonStriker, bowler, bowlingSquad);

            // Feed directly into authoritative Scoring Engine!
            DeliveryResponse res = scoringService.recordDelivery(currentInnings.getId(), req);

            if (res.isMatchComplete()) {
                stopSimulation(matchId);
            }
        } catch (Exception e) {
            System.err.println("Error during simulation tick for match " + matchId + ": " + e.getMessage());
        }
    }

    private DeliveryRequest generatePlausibleDelivery(Player striker, Player nonStriker, Player bowler, List<Player> fielders) {
        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(striker.getId());
        req.setNonStrikerId(nonStriker.getId());
        req.setBowlerId(bowler.getId());

        int roll = random.nextInt(100);

        if (roll < 34) {
            // Dot ball (34%)
            req.setBatterRuns(0);
        } else if (roll < 66) {
            // Single (32%)
            req.setBatterRuns(1);
        } else if (roll < 78) {
            // Two runs (12%)
            req.setBatterRuns(2);
        } else if (roll < 80) {
            // Three runs (2%)
            req.setBatterRuns(3);
        } else if (roll < 90) {
            // Boundary Four (10%)
            req.setBatterRuns(4);
        } else if (roll < 95) {
            // Maximum Six (5%)
            req.setBatterRuns(6);
        } else if (roll < 97) {
            // Wide (2%)
            req.setExtraType(ExtraType.WIDE);
            req.setExtraRuns(1);
            req.setBatterRuns(0);
        } else if (roll < 98) {
            // No Ball (1%)
            req.setExtraType(ExtraType.NO_BALL);
            req.setExtraRuns(1);
            req.setBatterRuns(1);
        } else {
            // Wicket (2-3%)
            req.setWicket(true);
            WicketType[] dismissals = {WicketType.CAUGHT, WicketType.BOWLED, WicketType.LBW, WicketType.RUN_OUT, WicketType.STUMPED};
            WicketType wType = dismissals[random.nextInt(dismissals.length)];
            req.setWicketType(wType);
            req.setDismissedPlayerId(striker.getId());
            if (wType == WicketType.CAUGHT || wType == WicketType.RUN_OUT || wType == WicketType.STUMPED) {
                if (!fielders.isEmpty()) {
                    req.setFielderId(fielders.get(random.nextInt(fielders.size())).getId());
                }
            }
        }

        return req;
    }

    @PreDestroy
    public void cleanup() {
        for (ScheduledFuture<?> future : activeSimulations.values()) {
            future.cancel(true);
        }
        scheduler.shutdown();
    }
}
