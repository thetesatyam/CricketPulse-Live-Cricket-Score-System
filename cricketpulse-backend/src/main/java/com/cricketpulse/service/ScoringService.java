package com.cricketpulse.service;

import com.cricketpulse.dto.CorrectionRequest;
import com.cricketpulse.dto.DeliveryRequest;
import com.cricketpulse.dto.DeliveryResponse;
import com.cricketpulse.entity.*;
import com.cricketpulse.exception.BadRequestException;
import com.cricketpulse.exception.ResourceNotFoundException;
import com.cricketpulse.exception.ScoringException;
import com.cricketpulse.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScoringService {

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final DeliveryRepository deliveryRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMatchStatsRepository statsRepository;
    private final AuditService auditService;
    private final SimpMessagingTemplate messagingTemplate;

    public ScoringService(MatchRepository matchRepository,
                          InningsRepository inningsRepository,
                          DeliveryRepository deliveryRepository,
                          PlayerRepository playerRepository,
                          PlayerMatchStatsRepository statsRepository,
                          AuditService auditService,
                          SimpMessagingTemplate messagingTemplate) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.deliveryRepository = deliveryRepository;
        this.playerRepository = playerRepository;
        this.statsRepository = statsRepository;
        this.auditService = auditService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public DeliveryResponse recordDelivery(Long inningsId, DeliveryRequest request) {
        Innings innings = inningsRepository.findById(inningsId)
                .orElseThrow(() -> new ResourceNotFoundException("Innings not found with ID: " + inningsId));

        Match match = innings.getMatch();

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.ABANDONED) {
            throw new ScoringException("Cannot record delivery: Match is already " + match.getStatus());
        }

        if (innings.getStatus() == InningsStatus.COMPLETED) {
            throw new ScoringException("Cannot record delivery: Innings is already completed");
        }

        Player striker = playerRepository.findById(request.getStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Striker not found with ID: " + request.getStrikerId()));
        Player nonStriker = playerRepository.findById(request.getNonStrikerId())
                .orElseThrow(() -> new ResourceNotFoundException("Non-striker not found with ID: " + request.getNonStrikerId()));
        Player bowler = playerRepository.findById(request.getBowlerId())
                .orElseThrow(() -> new ResourceNotFoundException("Bowler not found with ID: " + request.getBowlerId()));

        if (striker.getId().equals(nonStriker.getId())) {
            throw new BadRequestException("Striker and non-striker cannot be the same player");
        }
        if (striker.getId().equals(bowler.getId()) || nonStriker.getId().equals(bowler.getId())) {
            throw new BadRequestException("Bowler cannot be currently batting (striker/non-striker)");
        }

        // Determine if delivery is legal
        boolean isLegal = true;
        ExtraType extraType = request.getExtraType() != null ? request.getExtraType() : ExtraType.NONE;
        int extraRuns = request.getExtraRuns();

        if (extraType == ExtraType.WIDE || extraType == ExtraType.NO_BALL) {
            isLegal = false;
            if (extraRuns <= 0) {
                extraRuns = 1; // Default 1 penalty run for wide/no-ball
            }
        }

        int currentLegalBalls = innings.getLegalBalls();
        int overNumber = currentLegalBalls / 6;
        int ballNumber = isLegal ? (currentLegalBalls % 6) + 1 : (currentLegalBalls % 6);

        int nextSequence = deliveryRepository.findMaxSequenceNumberByInningsId(inningsId).orElse(0) + 1;

        Delivery delivery = new Delivery();
        delivery.setInnings(innings);
        delivery.setSequenceNumber(nextSequence);
        delivery.setOverNumber(overNumber);
        delivery.setBallNumber(ballNumber);
        delivery.setStriker(striker);
        delivery.setNonStriker(nonStriker);
        delivery.setBowler(bowler);
        delivery.setBatterRuns(request.getBatterRuns());
        delivery.setExtraType(extraType);
        delivery.setExtraRuns(extraRuns);
        delivery.setLegalDelivery(isLegal);
        delivery.setWicket(request.isWicket());
        delivery.setWicketType(request.getWicketType());

        // Handle wicket participant
        if (request.isWicket()) {
            Player dismissed = request.getDismissedPlayerId() != null
                    ? playerRepository.findById(request.getDismissedPlayerId()).orElse(striker)
                    : striker;
            delivery.setDismissedPlayer(dismissed);

            if (request.getFielderId() != null) {
                playerRepository.findById(request.getFielderId()).ifPresent(delivery::setFielder);
            }
        }

        // Build default commentary if not provided
        if (request.getCommentary() == null || request.getCommentary().isBlank()) {
            delivery.setCommentary(generateCommentaryText(delivery));
        } else {
            delivery.setCommentary(request.getCommentary());
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Update Innings state
        int deliveryTotalRuns = request.getBatterRuns() + extraRuns;
        innings.setTotalRuns(innings.getTotalRuns() + deliveryTotalRuns);

        if (extraType != ExtraType.NONE) {
            innings.setExtras(innings.getExtras() + extraRuns);
            switch (extraType) {
                case WIDE -> innings.setWideRuns(innings.getWideRuns() + extraRuns);
                case NO_BALL -> innings.setNoBallRuns(innings.getNoBallRuns() + extraRuns);
                case BYE -> innings.setByeRuns(innings.getByeRuns() + extraRuns);
                case LEG_BYE -> innings.setLegByeRuns(innings.getLegByeRuns() + extraRuns);
                case PENALTY -> innings.setPenaltyRuns(innings.getPenaltyRuns() + extraRuns);
                default -> {}
            }
        }

        if (isLegal) {
            innings.setLegalBalls(innings.getLegalBalls() + 1);
        }

        if (request.isWicket()) {
            innings.setWickets(innings.getWickets() + 1);
        }

        // Update Batter stats
        PlayerMatchStats strikerStats = getOrCreateStats(match, striker, innings.getBattingTeam());
        strikerStats.setRunsScored(strikerStats.getRunsScored() + request.getBatterRuns());
        if (extraType != ExtraType.WIDE) {
            // Wides are not counted as balls faced by the batter
            strikerStats.setBallsFaced(strikerStats.getBallsFaced() + 1);
        }
        if (request.getBatterRuns() == 4) strikerStats.setFours(strikerStats.getFours() + 1);
        if (request.getBatterRuns() == 6) strikerStats.setSixes(strikerStats.getSixes() + 1);

        if (request.isWicket() && delivery.getDismissedPlayer() != null && delivery.getDismissedPlayer().getId().equals(striker.getId())) {
            strikerStats.setOut(true);
            strikerStats.setDismissalInfo(formatDismissal(delivery));
        }
        statsRepository.save(strikerStats);

        // Update Non-striker stats if non-striker was run out
        if (request.isWicket() && delivery.getDismissedPlayer() != null && delivery.getDismissedPlayer().getId().equals(nonStriker.getId())) {
            PlayerMatchStats nonStrikerStats = getOrCreateStats(match, nonStriker, innings.getBattingTeam());
            nonStrikerStats.setOut(true);
            nonStrikerStats.setDismissalInfo(formatDismissal(delivery));
            statsRepository.save(nonStrikerStats);
        }

        // Update Bowler stats
        PlayerMatchStats bowlerStats = getOrCreateStats(match, bowler, innings.getBowlingTeam());
        if (isLegal) {
            bowlerStats.setLegalBallsBowled(bowlerStats.getLegalBallsBowled() + 1);
        }
        // Runs conceded by bowler includes batter runs, wides, and no-balls (byes and leg-byes are fielding extras)
        int bowlerRunsConceded = request.getBatterRuns();
        if (extraType == ExtraType.WIDE) {
            bowlerRunsConceded += extraRuns;
            bowlerStats.setWidesConceded(bowlerStats.getWidesConceded() + extraRuns);
        } else if (extraType == ExtraType.NO_BALL) {
            bowlerRunsConceded += extraRuns;
            bowlerStats.setNoBallsConceded(bowlerStats.getNoBallsConceded() + extraRuns);
        }
        bowlerStats.setRunsConceded(bowlerStats.getRunsConceded() + bowlerRunsConceded);

        if (request.isWicket() && request.getWicketType() != null && request.getWicketType().isCreditedToBowler()) {
            bowlerStats.setWicketsTaken(bowlerStats.getWicketsTaken() + 1);
        }
        statsRepository.save(bowlerStats);

        // Update Fielder stats if catch / run out / stumping
        if (delivery.getFielder() != null && request.isWicket()) {
            PlayerMatchStats fielderStats = getOrCreateStats(match, delivery.getFielder(), innings.getBowlingTeam());
            if (request.getWicketType() == WicketType.CAUGHT) {
                fielderStats.setCatches(fielderStats.getCatches() + 1);
            } else if (request.getWicketType() == WicketType.RUN_OUT) {
                fielderStats.setRunOuts(fielderStats.getRunOuts() + 1);
            } else if (request.getWicketType() == WicketType.STUMPED) {
                fielderStats.setStumpings(fielderStats.getStumpings() + 1);
            }
            statsRepository.save(fielderStats);
        }

        // Strike rotation logic
        Long nextStrikerId = striker.getId();
        Long nextNonStrikerId = nonStriker.getId();

        int physicalRuns = request.getBatterRuns();
        if (extraType == ExtraType.BYE || extraType == ExtraType.LEG_BYE) {
            physicalRuns += extraRuns;
        }

        // Rotate on odd runs
        if (physicalRuns % 2 != 0) {
            Long temp = nextStrikerId;
            nextStrikerId = nextNonStrikerId;
            nextNonStrikerId = temp;
        }

        // Check if over completed (6 legal balls in current over)
        boolean isOverComplete = isLegal && (innings.getLegalBalls() % 6 == 0);
        if (isOverComplete) {
            // At the end of over, batsmen switch ends
            Long temp = nextStrikerId;
            nextStrikerId = nextNonStrikerId;
            nextNonStrikerId = temp;
        }

        // Check Innings and Match Completion
        boolean isInningsComplete = false;
        boolean isMatchComplete = false;
        String matchResult = null;

        int maxLegalBalls = match.getOversPerInnings() * 6;
        boolean allOut = innings.getWickets() >= 10;
        boolean oversFinished = innings.getLegalBalls() >= maxLegalBalls;
        boolean targetPassed = innings.getTarget() != null && innings.getTotalRuns() >= innings.getTarget();

        if (targetPassed || allOut || oversFinished) {
            isInningsComplete = true;
            innings.setStatus(InningsStatus.COMPLETED);

            if (innings.getInningsNumber() == 1) {
                // First innings completed -> start innings break
                match.setStatus(MatchStatus.INNINGS_BREAK);
                match.setCurrentInningsNumber(2);
                matchRepository.save(match);
                // Create second innings
                createSecondInnings(match, innings.getTotalRuns() + 1);
            } else {
                // Second innings completed -> calculate match result
                isMatchComplete = true;
                match.setStatus(MatchStatus.COMPLETED);
                matchResult = calculateMatchResult(match, innings);
                match.setResult(matchResult);
                matchRepository.save(match);
            }
        }

        inningsRepository.save(innings);

        // Build Response
        DeliveryResponse res = DeliveryResponse.fromEntity(savedDelivery);
        res.setTotalRuns(innings.getTotalRuns());
        res.setWickets(innings.getWickets());
        res.setOversFormatted(innings.getOversFormatted());
        res.setCurrentRunRate(innings.getRunRate());
        res.setNextStrikerId(nextStrikerId);
        res.setNextNonStrikerId(nextNonStrikerId);
        res.setOverComplete(isOverComplete);
        res.setInningsComplete(isInningsComplete);
        res.setMatchComplete(isMatchComplete);
        res.setMatchResult(matchResult);

        // Broadcast to WebSocket subscribers
        broadcastScoreUpdate(match.getId(), res);

        return res;
    }

    @Transactional
    public DeliveryResponse applyCorrection(CorrectionRequest request) {
        Delivery delivery = deliveryRepository.findById(request.getDeliveryId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + request.getDeliveryId()));

        if (delivery.isReversed()) {
            throw new BadRequestException("Delivery is already marked as reversed");
        }

        delivery.setReversed(true);
        delivery.setReversalReason(request.getReason());
        deliveryRepository.save(delivery);

        Innings innings = delivery.getInnings();
        recalculateInningsState(innings);

        auditService.log("DELIVERY_CORRECTION", "Delivery", delivery.getId(),
                "Reversed delivery " + delivery.getId() + ". Reason: " + request.getReason());

        DeliveryResponse res = DeliveryResponse.fromEntity(delivery);
        res.setTotalRuns(innings.getTotalRuns());
        res.setWickets(innings.getWickets());
        res.setOversFormatted(innings.getOversFormatted());
        res.setCurrentRunRate(innings.getRunRate());

        broadcastScoreUpdate(innings.getMatch().getId(), res);
        return res;
    }

    private void recalculateInningsState(Innings innings) {
        List<Delivery> activeDeliveries = deliveryRepository
                .findByInningsIdOrderBySequenceNumberAsc(innings.getId())
                .stream().filter(d -> !d.isReversed()).toList();

        int totalRuns = 0;
        int extras = 0;
        int wideRuns = 0;
        int noBallRuns = 0;
        int byeRuns = 0;
        int legByeRuns = 0;
        int penaltyRuns = 0;
        int legalBalls = 0;
        int wickets = 0;

        for (Delivery d : activeDeliveries) {
            totalRuns += d.getTotalDeliveryRuns();
            if (d.getExtraType() != ExtraType.NONE) {
                extras += d.getExtraRuns();
                switch (d.getExtraType()) {
                    case WIDE -> wideRuns += d.getExtraRuns();
                    case NO_BALL -> noBallRuns += d.getExtraRuns();
                    case BYE -> byeRuns += d.getExtraRuns();
                    case LEG_BYE -> legByeRuns += d.getExtraRuns();
                    case PENALTY -> penaltyRuns += d.getExtraRuns();
                    default -> {}
                }
            }
            if (d.isLegalDelivery()) legalBalls++;
            if (d.isWicket()) wickets++;
        }

        innings.setTotalRuns(totalRuns);
        innings.setExtras(extras);
        innings.setWideRuns(wideRuns);
        innings.setNoBallRuns(noBallRuns);
        innings.setByeRuns(byeRuns);
        innings.setLegByeRuns(legByeRuns);
        innings.setPenaltyRuns(penaltyRuns);
        innings.setLegalBalls(legalBalls);
        innings.setWickets(wickets);

        inningsRepository.save(innings);
    }

    private void createSecondInnings(Match match, int target) {
        List<Innings> existing = inningsRepository.findByMatchIdOrderByInningsNumberAsc(match.getId());
        if (existing.size() >= 2) return;

        Innings firstInnings = existing.get(0);
        Innings secondInnings = new Innings();
        secondInnings.setMatch(match);
        secondInnings.setInningsNumber(2);
        secondInnings.setBattingTeam(firstInnings.getBowlingTeam());
        secondInnings.setBowlingTeam(firstInnings.getBattingTeam());
        secondInnings.setTarget(target);
        secondInnings.setStatus(InningsStatus.IN_PROGRESS);
        inningsRepository.save(secondInnings);
    }

    private String calculateMatchResult(Match match, Innings secondInnings) {
        List<Innings> allInnings = inningsRepository.findByMatchIdOrderByInningsNumberAsc(match.getId());
        if (allInnings.isEmpty()) return "Match completed";

        Innings firstInnings = allInnings.get(0);
        int runs1 = firstInnings.getTotalRuns();
        int runs2 = secondInnings.getTotalRuns();

        if (runs2 > runs1) {
            int wicketsRemaining = 10 - secondInnings.getWickets();
            match.setWinnerTeam(secondInnings.getBattingTeam());
            return secondInnings.getBattingTeam().getName() + " won by " + wicketsRemaining + " wickets";
        } else if (runs1 > runs2) {
            int runMargin = runs1 - runs2;
            match.setWinnerTeam(firstInnings.getBattingTeam());
            return firstInnings.getBattingTeam().getName() + " won by " + runMargin + " runs";
        } else {
            return "Match tied";
        }
    }

    private PlayerMatchStats getOrCreateStats(Match match, Player player, Team team) {
        return statsRepository.findByMatchIdAndPlayerId(match.getId(), player.getId())
                .orElseGet(() -> {
                    PlayerMatchStats s = new PlayerMatchStats(match, player, team);
                    return statsRepository.save(s);
                });
    }

    private String formatDismissal(Delivery delivery) {
        if (!delivery.isWicket() || delivery.getWicketType() == null) return "not out";
        return switch (delivery.getWicketType()) {
            case BOWLED -> "b " + delivery.getBowler().getFullName();
            case CAUGHT -> delivery.getFielder() != null
                    ? "c " + delivery.getFielder().getFullName() + " b " + delivery.getBowler().getFullName()
                    : "c & b " + delivery.getBowler().getFullName();
            case LBW -> "lbw b " + delivery.getBowler().getFullName();
            case RUN_OUT -> delivery.getFielder() != null
                    ? "run out (" + delivery.getFielder().getFullName() + ")"
                    : "run out";
            case STUMPED -> delivery.getFielder() != null
                    ? "st " + delivery.getFielder().getFullName() + " b " + delivery.getBowler().getFullName()
                    : "st b " + delivery.getBowler().getFullName();
            case HIT_WICKET -> "hit wicket b " + delivery.getBowler().getFullName();
            case RETIRED_HURT -> "retired hurt";
        };
    }

    private String generateCommentaryText(Delivery d) {
        if (d.isWicket()) {
            return "OUT! " + (d.getDismissedPlayer() != null ? d.getDismissedPlayer().getFullName() : "Batter") +
                    " departs! " + d.getWicketType() + " for " + d.getBowler().getFullName() + "!";
        }
        if (d.getExtraType() == ExtraType.WIDE) {
            return "Wide ball called by the umpire. 1 extra run added.";
        }
        if (d.getExtraType() == ExtraType.NO_BALL) {
            return "No-ball called! Overstepping by " + d.getBowler().getFullName() + ". Free hit coming up!";
        }
        return switch (d.getBatterRuns()) {
            case 6 -> "SIX! Smacked high and handsome into the stands! What a sensational stroke!";
            case 4 -> "FOUR! Pierces the infield with precision and finds the boundary rope!";
            case 0 -> "No run. Defended solidly back down the pitch.";
            case 1 -> "Single taken. Pushed into the gap for a quick run.";
            case 2 -> "Good running between the wickets, scampering back for a comfortable brace.";
            case 3 -> "Great placement through the covers, the batsmen hustle back for three.";
            default -> d.getBatterRuns() + " runs scored.";
        };
    }

    private void broadcastScoreUpdate(Long matchId, DeliveryResponse response) {
        try {
            messagingTemplate.convertAndSend("/topic/matches/" + matchId + "/score", response);
            messagingTemplate.convertAndSend("/topic/matches/" + matchId + "/commentary", response);
        } catch (Exception e) {
            // Log but don't fail transactional scoring
            System.err.println("WebSocket broadcast error: " + e.getMessage());
        }
    }
}
