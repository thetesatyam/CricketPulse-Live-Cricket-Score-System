package com.cricketpulse.service;

import com.cricketpulse.dto.*;
import com.cricketpulse.entity.*;
import com.cricketpulse.exception.BadRequestException;
import com.cricketpulse.exception.ResourceNotFoundException;
import com.cricketpulse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final DeliveryRepository deliveryRepository;
    private final TeamRepository teamRepository;
    private final VenueRepository venueRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final PlayerMatchStatsRepository statsRepository;
    private final AuditService auditService;

    public MatchService(MatchRepository matchRepository,
                        InningsRepository inningsRepository,
                        DeliveryRepository deliveryRepository,
                        TeamRepository teamRepository,
                        VenueRepository venueRepository,
                        TournamentRepository tournamentRepository,
                        PlayerRepository playerRepository,
                        MatchPlayerRepository matchPlayerRepository,
                        PlayerMatchStatsRepository statsRepository,
                        AuditService auditService) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.deliveryRepository = deliveryRepository;
        this.teamRepository = teamRepository;
        this.venueRepository = venueRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.statsRepository = statsRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getAllMatches() {
        return matchRepository.findAllByOrderByScheduledStartTimeDesc().stream()
                .map(this::enrichMatchSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getLiveMatches() {
        return matchRepository.findByStatusIn(List.of(MatchStatus.LIVE, MatchStatus.INNINGS_BREAK)).stream()
                .map(this::enrichMatchSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getUpcomingMatches() {
        return matchRepository.findByStatus(MatchStatus.SCHEDULED).stream()
                .map(this::enrichMatchSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getCompletedMatches() {
        return matchRepository.findByStatus(MatchStatus.COMPLETED).stream()
                .map(this::enrichMatchSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatchDetailDto getMatchDetails(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        MatchDetailDto detail = new MatchDetailDto();
        detail.setSummary(enrichMatchSummary(match));

        List<Innings> inningsList = inningsRepository.findByMatchIdOrderByInningsNumberAsc(matchId);
        List<InningsDto> inningsDtos = inningsList.stream().map(this::mapInningsToDto).collect(Collectors.toList());
        detail.setInnings(inningsDtos);

        if (!inningsList.isEmpty()) {
            Innings currentInnings = inningsList.get(Math.min(match.getCurrentInningsNumber() - 1, inningsList.size() - 1));

            // Target calculations
            if (currentInnings.getTarget() != null) {
                detail.setTarget(currentInnings.getTarget());
                int runsNeeded = Math.max(0, currentInnings.getTarget() - currentInnings.getTotalRuns());
                int ballsRemaining = Math.max(0, (match.getOversPerInnings() * 6) - currentInnings.getLegalBalls());
                detail.setRunsNeeded(runsNeeded);
                detail.setBallsRemaining(ballsRemaining);
                if (ballsRemaining > 0) {
                    double rrr = ((double) runsNeeded / (ballsRemaining / 6.0));
                    detail.setRequiredRunRate(Math.round(rrr * 100.0) / 100.0);
                }
            }

            // Recent deliveries (last 12 balls)
            List<Delivery> latestDeliveries = deliveryRepository
                    .findByInningsIdAndIsReversedFalseOrderBySequenceNumberDesc(currentInnings.getId());

            List<String> recentBalls = new ArrayList<>();
            for (int i = 0; i < Math.min(12, latestDeliveries.size()); i++) {
                Delivery d = latestDeliveries.get(i);
                if (d.isWicket()) recentBalls.add("W");
                else if (d.getExtraType() == ExtraType.WIDE) recentBalls.add("wd");
                else if (d.getExtraType() == ExtraType.NO_BALL) recentBalls.add("nb");
                else recentBalls.add(String.valueOf(d.getBatterRuns()));
            }
            Collections.reverse(recentBalls);
            detail.setRecentBalls(recentBalls);

            // Populate on-field active striker, non-striker, bowler if available
            if (!latestDeliveries.isEmpty()) {
                Delivery last = latestDeliveries.get(0);
                detail.setStriker(getBatterScore(matchId, last.getStriker()));
                detail.setNonStriker(getBatterScore(matchId, last.getNonStriker()));
                detail.setCurrentBowler(getBowlerScore(matchId, last.getBowler()));
            }
        }

        return detail;
    }

    @Transactional(readOnly = true)
    public ScorecardDto getScorecard(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        ScorecardDto sc = new ScorecardDto();
        sc.setMatchId(match.getId());
        sc.setMatchTitle(match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
        sc.setStatus(match.getStatus().name());
        sc.setResult(match.getResult());

        List<Innings> inningsList = inningsRepository.findByMatchIdOrderByInningsNumberAsc(matchId);
        sc.setInnings(inningsList.stream().map(this::mapInningsToDto).collect(Collectors.toList()));
        return sc;
    }

    @Transactional(readOnly = true)
    public List<CommentaryDto> getMatchCommentary(Long matchId) {
        List<Delivery> deliveries = deliveryRepository.findLatestDeliveriesByMatchId(matchId);
        return deliveries.stream().map(CommentaryDto::fromDelivery).collect(Collectors.toList());
    }

    @Transactional
    public MatchSummaryDto createMatch(CreateMatchRequest request) {
        Team homeTeam = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team not found: " + request.getHomeTeamId()));
        Team awayTeam = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team not found: " + request.getAwayTeamId()));

        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new BadRequestException("Home and away teams must be different");
        }

        Match match = new Match();
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setMatchType(request.getMatchType());
        match.setOversPerInnings(request.getOversPerInnings() > 0 ? request.getOversPerInnings() : request.getMatchType().getDefaultOvers());
        match.setScheduledStartTime(request.getScheduledStartTime());
        match.setStatus(MatchStatus.SCHEDULED);
        match.setCurrentInningsNumber(1);

        if (request.getVenueId() != null) {
            venueRepository.findById(request.getVenueId()).ifPresent(match::setVenue);
        }
        if (request.getTournamentId() != null) {
            tournamentRepository.findById(request.getTournamentId()).ifPresent(match::setTournament);
        }

        Match saved = matchRepository.save(match);

        // Populate match squads automatically from team player rosters
        populateMatchSquad(saved, homeTeam);
        populateMatchSquad(saved, awayTeam);

        // Pre-create 1st Innings
        Innings innings1 = new Innings();
        innings1.setMatch(saved);
        innings1.setInningsNumber(1);
        innings1.setBattingTeam(homeTeam);
        innings1.setBowlingTeam(awayTeam);
        innings1.setStatus(InningsStatus.IN_PROGRESS);
        inningsRepository.save(innings1);

        auditService.log("MATCH_CREATE", "Match", saved.getId(), "Scheduled match: " + homeTeam.getShortName() + " vs " + awayTeam.getShortName());
        return enrichMatchSummary(saved);
    }

    @Transactional
    public MatchSummaryDto startMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        match.setStatus(MatchStatus.LIVE);
        Match updated = matchRepository.save(match);
        auditService.log("MATCH_START", "Match", updated.getId(), "Match started live");
        return enrichMatchSummary(updated);
    }

    private void populateMatchSquad(Match match, Team team) {
        List<Player> players = playerRepository.findByTeamId(team.getId());
        int order = 1;
        for (Player p : players) {
            MatchPlayer mp = new MatchPlayer(match, team, p, order == 1, order == 4, order++);
            matchPlayerRepository.save(mp);
        }
    }

    private MatchSummaryDto enrichMatchSummary(Match match) {
        MatchSummaryDto dto = MatchSummaryDto.fromEntity(match);

        List<Innings> inningsList = inningsRepository.findByMatchIdOrderByInningsNumberAsc(match.getId());
        if (!inningsList.isEmpty()) {
            Innings in1 = inningsList.get(0);
            dto.setInnings1Score(in1.getBattingTeam().getShortName() + " " + in1.getTotalRuns() + "/" + in1.getWickets() + " (" + in1.getOversFormatted() + ")");

            if (inningsList.size() > 1) {
                Innings in2 = inningsList.get(1);
                dto.setInnings2Score(in2.getBattingTeam().getShortName() + " " + in2.getTotalRuns() + "/" + in2.getWickets() + " (" + in2.getOversFormatted() + ")");
                dto.setLiveScore(dto.getInnings2Score());
                dto.setCurrentRunRate(in2.getRunRate());
                if (in2.getTarget() != null && (match.getOversPerInnings() * 6 - in2.getLegalBalls()) > 0) {
                    int ballsLeft = (match.getOversPerInnings() * 6 - in2.getLegalBalls());
                    int runsLeft = Math.max(0, in2.getTarget() - in2.getTotalRuns());
                    dto.setRequiredRunRate(Math.round(((double) runsLeft / (ballsLeft / 6.0)) * 100.0) / 100.0);
                }
            } else {
                dto.setLiveScore(dto.getInnings1Score());
                dto.setCurrentRunRate(in1.getRunRate());
            }
        }

        return dto;
    }

    private InningsDto mapInningsToDto(Innings in) {
        InningsDto dto = new InningsDto();
        dto.setId(in.getId());
        dto.setInningsNumber(in.getInningsNumber());
        dto.setBattingTeamId(in.getBattingTeam().getId());
        dto.setBattingTeamName(in.getBattingTeam().getName());
        dto.setBattingTeamShortName(in.getBattingTeam().getShortName());
        dto.setBowlingTeamId(in.getBowlingTeam().getId());
        dto.setBowlingTeamName(in.getBowlingTeam().getName());
        dto.setBowlingTeamShortName(in.getBowlingTeam().getShortName());
        dto.setTotalRuns(in.getTotalRuns());
        dto.setWickets(in.getWickets());
        dto.setLegalBalls(in.getLegalBalls());
        dto.setOversFormatted(in.getOversFormatted());
        dto.setRunRate(in.getRunRate());
        dto.setExtras(in.getExtras());
        dto.setWideRuns(in.getWideRuns());
        dto.setNoBallRuns(in.getNoBallRuns());
        dto.setByeRuns(in.getByeRuns());
        dto.setLegByeRuns(in.getLegByeRuns());
        dto.setTarget(in.getTarget());
        dto.setStatus(in.getStatus());

        // Batters scorecard
        List<PlayerMatchStats> batters = statsRepository.findByMatchIdAndTeamId(in.getMatch().getId(), in.getBattingTeam().getId())
                .stream().filter(s -> s.getBallsFaced() > 0 || s.isOut()).toList();
        dto.setBatters(batters.stream().map(this::mapBatterScore).collect(Collectors.toList()));

        // Bowlers scorecard
        List<PlayerMatchStats> bowlers = statsRepository.findByMatchIdAndTeamId(in.getMatch().getId(), in.getBowlingTeam().getId())
                .stream().filter(s -> s.getLegalBallsBowled() > 0).toList();
        dto.setBowlers(bowlers.stream().map(this::mapBowlerScore).collect(Collectors.toList()));

        return dto;
    }

    private BatterScoreDto mapBatterScore(PlayerMatchStats s) {
        BatterScoreDto dto = new BatterScoreDto();
        dto.setPlayerId(s.getPlayer().getId());
        dto.setPlayerName(s.getPlayer().getFullName());
        dto.setRuns(s.getRunsScored());
        dto.setBalls(s.getBallsFaced());
        dto.setFours(s.getFours());
        dto.setSixes(s.getSixes());
        dto.setStrikeRate(s.getStrikeRate());
        dto.setOut(s.isOut());
        dto.setDismissalInfo(s.getDismissalInfo() != null ? s.getDismissalInfo() : "not out");
        return dto;
    }

    private BowlerScoreDto mapBowlerScore(PlayerMatchStats s) {
        BowlerScoreDto dto = new BowlerScoreDto();
        dto.setPlayerId(s.getPlayer().getId());
        dto.setPlayerName(s.getPlayer().getFullName());
        dto.setOvers(s.getOversBowled());
        dto.setMaidens(s.getMaidenOvers());
        dto.setRuns(s.getRunsConceded());
        dto.setWickets(s.getWicketsTaken());
        dto.setEconomy(s.getEconomyRate());
        dto.setWides(s.getWidesConceded());
        dto.setNoBalls(s.getNoBallsConceded());
        return dto;
    }

    private BatterScoreDto getBatterScore(Long matchId, Player p) {
        return statsRepository.findByMatchIdAndPlayerId(matchId, p.getId())
                .map(this::mapBatterScore)
                .orElseGet(() -> {
                    BatterScoreDto d = new BatterScoreDto();
                    d.setPlayerId(p.getId());
                    d.setPlayerName(p.getFullName());
                    d.setDismissalInfo("not out");
                    return d;
                });
    }

    private BowlerScoreDto getBowlerScore(Long matchId, Player p) {
        return statsRepository.findByMatchIdAndPlayerId(matchId, p.getId())
                .map(this::mapBowlerScore)
                .orElseGet(() -> {
                    BowlerScoreDto d = new BowlerScoreDto();
                    d.setPlayerId(p.getId());
                    d.setPlayerName(p.getFullName());
                    d.setOvers("0.0");
                    return d;
                });
    }
}
