package com.cricketpulse.dto;

import java.util.ArrayList;
import java.util.List;

public class ScorecardDto {
    private Long matchId;
    private String matchTitle;
    private String status;
    private String result;
    private List<InningsDto> innings = new ArrayList<>();

    public ScorecardDto() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getMatchTitle() { return matchTitle; }
    public void setMatchTitle(String matchTitle) { this.matchTitle = matchTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public List<InningsDto> getInnings() { return innings; }
    public void setInnings(List<InningsDto> innings) { this.innings = innings; }
}
