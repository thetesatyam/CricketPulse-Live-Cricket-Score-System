package com.cricketpulse.service;

import com.cricketpulse.dto.CorrectionRequest;
import com.cricketpulse.dto.DeliveryRequest;
import com.cricketpulse.dto.DeliveryResponse;
import com.cricketpulse.entity.*;
import com.cricketpulse.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoringEngineTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private InningsRepository inningsRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerMatchStatsRepository statsRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ScoringService scoringService;

    private Match testMatch;
    private Innings testInnings;
    private Team teamIndia;
    private Team teamAustralia;
    private Player striker;
    private Player nonStriker;
    private Player bowler;

    @BeforeEach
    void setUp() {
        teamIndia = new Team("India", "IND", "India", null);
        teamIndia.setId(1L);

        teamAustralia = new Team("Australia", "AUS", "Australia", null);
        teamAustralia.setId(2L);

        striker = new Player("Rohit Sharma", teamIndia, "Right-hand bat", "None", PlayingRole.BATTER);
        striker.setId(10L);

        nonStriker = new Player("Virat Kohli", teamIndia, "Right-hand bat", "None", PlayingRole.BATTER);
        nonStriker.setId(11L);

        bowler = new Player("Pat Cummins", teamAustralia, "Right-hand bat", "Right-arm fast", PlayingRole.BOWLER);
        bowler.setId(20L);

        testMatch = new Match();
        testMatch.setId(100L);
        testMatch.setHomeTeam(teamIndia);
        testMatch.setAwayTeam(teamAustralia);
        testMatch.setMatchType(MatchType.T20);
        testMatch.setOversPerInnings(20);
        testMatch.setStatus(MatchStatus.LIVE);
        testMatch.setCurrentInningsNumber(1);
        testMatch.setScheduledStartTime(LocalDateTime.now());

        testInnings = new Innings();
        testInnings.setId(200L);
        testInnings.setMatch(testMatch);
        testInnings.setInningsNumber(1);
        testInnings.setBattingTeam(teamIndia);
        testInnings.setBowlingTeam(teamAustralia);
        testInnings.setStatus(InningsStatus.IN_PROGRESS);
        testInnings.setTotalRuns(0);
        testInnings.setWickets(0);
        testInnings.setLegalBalls(0);
        testInnings.setExtras(0);

        lenient().when(inningsRepository.findById(200L)).thenReturn(Optional.of(testInnings));
        lenient().when(playerRepository.findById(10L)).thenReturn(Optional.of(striker));
        lenient().when(playerRepository.findById(11L)).thenReturn(Optional.of(nonStriker));
        lenient().when(playerRepository.findById(20L)).thenReturn(Optional.of(bowler));
        lenient().when(deliveryRepository.findMaxSequenceNumberByInningsId(200L)).thenReturn(Optional.of(0));
        lenient().when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> {
            Delivery d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });
        lenient().when(statsRepository.findByMatchIdAndPlayerId(any(), any())).thenReturn(Optional.empty());
        lenient().when(statsRepository.save(any(PlayerMatchStats.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Single run delivery updates total, increments legal ball, and rotates strike")
    void testSingleRunAndStrikeRotation() {
        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setBatterRuns(1);

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertNotNull(res);
        assertEquals(1, res.getTotalRuns());
        assertEquals(0, res.getWickets());
        assertEquals(1, testInnings.getLegalBalls());
        // Strike rotated: next striker should be non-striker (ID 11)
        assertEquals(11L, res.getNextStrikerId());
        assertEquals(10L, res.getNextNonStrikerId());
    }

    @Test
    @DisplayName("Boundary four keeps striker on strike if not over end")
    void testBoundaryFour() {
        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setBatterRuns(4);

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertEquals(4, res.getTotalRuns());
        assertEquals(1, testInnings.getLegalBalls());
        // Even runs: striker stays on strike
        assertEquals(10L, res.getNextStrikerId());
        assertEquals(11L, res.getNextNonStrikerId());
    }

    @Test
    @DisplayName("Wide delivery adds extra run without incrementing legal balls")
    void testWideDelivery() {
        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setExtraType(ExtraType.WIDE);
        req.setExtraRuns(1);
        req.setBatterRuns(0);

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertEquals(1, res.getTotalRuns());
        assertEquals(0, testInnings.getLegalBalls()); // Must remain 0 legal balls
        assertEquals(1, testInnings.getWideRuns());
        assertEquals(1, testInnings.getExtras());
        assertFalse(res.isLegalDelivery());
    }

    @Test
    @DisplayName("End of over (6 legal balls) triggers strike rotation")
    void testEndOfOverStrikeRotation() {
        testInnings.setLegalBalls(5); // 5 balls bowled

        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setBatterRuns(0); // Dot ball on 6th ball

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertEquals(6, testInnings.getLegalBalls());
        assertTrue(res.isOverComplete());
        // Over completed: batsmen swap ends
        assertEquals(11L, res.getNextStrikerId());
        assertEquals(10L, res.getNextNonStrikerId());
    }

    @Test
    @DisplayName("Wicket delivery increments wickets and records dismissal")
    void testWicketDelivery() {
        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setWicket(true);
        req.setWicketType(WicketType.BOWLED);
        req.setDismissedPlayerId(10L);

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertTrue(res.isWicket());
        assertEquals(1, res.getWickets());
        assertEquals(1, testInnings.getWickets());
        assertEquals(WicketType.BOWLED, res.getWicketType());
    }

    @Test
    @DisplayName("Target reached in 2nd innings marks match as completed")
    void testTargetReachedInSecondInnings() {
        testInnings.setInningsNumber(2);
        testInnings.setTarget(150);
        testInnings.setTotalRuns(146);

        Innings firstInnings = new Innings();
        firstInnings.setTotalRuns(149);
        firstInnings.setBattingTeam(teamAustralia);
        firstInnings.setBowlingTeam(teamIndia);

        when(inningsRepository.findByMatchIdOrderByInningsNumberAsc(100L))
                .thenReturn(List.of(firstInnings, testInnings));

        DeliveryRequest req = new DeliveryRequest();
        req.setStrikerId(10L);
        req.setNonStrikerId(11L);
        req.setBowlerId(20L);
        req.setBatterRuns(6); // 146 + 6 = 152 >= 150

        DeliveryResponse res = scoringService.recordDelivery(200L, req);

        assertTrue(res.isInningsComplete());
        assertTrue(res.isMatchComplete());
        assertNotNull(res.getMatchResult());
        assertTrue(res.getMatchResult().contains("India won"));
        assertEquals(MatchStatus.COMPLETED, testMatch.getStatus());
    }

    @Test
    @DisplayName("Auditable correction reverses delivery and recomputes innings totals")
    void testAuditableCorrection() {
        Delivery delivery = new Delivery();
        delivery.setId(50L);
        delivery.setInnings(testInnings);
        delivery.setStriker(striker);
        delivery.setNonStriker(nonStriker);
        delivery.setBowler(bowler);
        delivery.setBatterRuns(4);
        delivery.setLegalDelivery(true);
        delivery.setReversed(false);

        when(deliveryRepository.findById(50L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.findByInningsIdOrderBySequenceNumberAsc(200L)).thenReturn(List.of(delivery));

        CorrectionRequest corReq = new CorrectionRequest(50L, "Scorer error: leg bye signaled instead of 4");
        DeliveryResponse res = scoringService.applyCorrection(corReq);

        assertTrue(delivery.isReversed());
        assertEquals("Scorer error: leg bye signaled instead of 4", delivery.getReversalReason());
        // Since the only delivery was reversed, active total runs and legal balls should become 0
        assertEquals(0, testInnings.getTotalRuns());
        assertEquals(0, testInnings.getLegalBalls());
    }
}
