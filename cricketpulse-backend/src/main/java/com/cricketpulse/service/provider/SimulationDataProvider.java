package com.cricketpulse.service.provider;

import com.cricketpulse.dto.MatchSummaryDto;
import com.cricketpulse.service.MatchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("simulationDataProvider")
@ConditionalOnProperty(name = "cricketpulse.provider.type", havingValue = "simulation", matchIfMissing = true)
public class SimulationDataProvider implements CricketDataProvider {

    private final MatchService matchService;

    public SimulationDataProvider(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public String getProviderName() {
        return "Internal Real-Time Simulator Engine";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public List<MatchSummaryDto> fetchLiveMatches() {
        return matchService.getLiveMatches();
    }

    @Override
    public MatchSummaryDto fetchMatchScore(String externalMatchId) {
        return matchService.getLiveMatches().stream().findFirst().orElse(null);
    }
}
