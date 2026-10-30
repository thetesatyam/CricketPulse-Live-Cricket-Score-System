package com.cricketpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTeamRequest {
    @NotBlank(message = "Team name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Short name is required")
    @Size(max = 10)
    private String shortName;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    private String logoUrl;

    public CreateTeamRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
