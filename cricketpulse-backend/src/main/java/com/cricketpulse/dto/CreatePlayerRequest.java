package com.cricketpulse.dto;

import com.cricketpulse.entity.PlayingRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePlayerRequest {
    @NotBlank(message = "Player full name is required")
    @Size(max = 100)
    private String fullName;

    private Long teamId;

    private String battingStyle;

    private String bowlingStyle;

    @NotNull(message = "Playing role is required")
    private PlayingRole playingRole;

    private String profileImageUrl;

    public CreatePlayerRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getBattingStyle() { return battingStyle; }
    public void setBattingStyle(String battingStyle) { this.battingStyle = battingStyle; }

    public String getBowlingStyle() { return bowlingStyle; }
    public void setBowlingStyle(String bowlingStyle) { this.bowlingStyle = bowlingStyle; }

    public PlayingRole getPlayingRole() { return playingRole; }
    public void setPlayingRole(PlayingRole playingRole) { this.playingRole = playingRole; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
