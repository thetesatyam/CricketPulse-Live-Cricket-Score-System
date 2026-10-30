package com.cricketpulse.service.provider;

import com.cricketpulse.dto.MatchSummaryDto;
import java.util.List;

public interface CricketDataProvider {
    String getProviderName();
    boolean isAvailable();
    List<MatchSummaryDto> fetchLiveMatches();
    MatchSummaryDto fetchMatchScore(String externalMatchId);
}
