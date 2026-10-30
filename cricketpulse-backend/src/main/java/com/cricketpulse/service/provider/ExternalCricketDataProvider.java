package com.cricketpulse.service.provider;

import com.cricketpulse.dto.MatchSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("externalCricketDataProvider")
@ConditionalOnProperty(name = "cricketpulse.provider.type", havingValue = "external")
public class ExternalCricketDataProvider implements CricketDataProvider {

    @Value("${cricketpulse.provider.api-key:}")
    private String apiKey;

    @Value("${cricketpulse.provider.base-url:https://api.cricapi.com/v1}")
    private String baseUrl;

    @Override
    public String getProviderName() {
        return "External Sports Data API Adapter";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public List<MatchSummaryDto> fetchLiveMatches() {
        if (!isAvailable()) {
            return Collections.emptyList();
        }
        // In real integration, dispatch HTTP request to baseUrl with apiKey
        return Collections.emptyList();
    }

    @Override
    public MatchSummaryDto fetchMatchScore(String externalMatchId) {
        if (!isAvailable()) {
            return null;
        }
        return null;
    }
}
