package com.softdesign.tourney.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;

    @NotEmpty(message = "Tournament name must not be empty")
    private String name;
    @Max(value = 1250000, message = "Prize money must not exceed 1,250,000")
    @Min(value = 0, message = "Prize money must be non-negative")
    private double prizeMoney;
    @NotEmpty(message = "Location must not be empty")
    private String location;
    @Max(value = 5000, message = "VRS points must not exceed 5000")
    @Min(value = 0, message = "VRS points must be non-negative")
    private int vrsPoints;

    /** Raw IDs stored in the DB — used for persistence. */
    private List<Long> teamIds;

    /**
     * Resolved team objects — populated by TournamentService before returning to
     * the controller, by calling team-service for each ID.
     * Never persisted; ignored by Jackson export strategies (transient in spirit).
     */
    @Builder.Default
    private List<TeamDto> teams = new ArrayList<>();

    // --- Explicit getters/setters kept alongside Lombok @Data for clarity ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrizeMoney() { return prizeMoney; }
    public void setPrizeMoney(double prizeMoney) { this.prizeMoney = prizeMoney; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getVrsPoints() { return vrsPoints; }
    public void setVrsPoints(int vrsPoints) { this.vrsPoints = vrsPoints; }

    public List<Long> getTeamIds() { return teamIds; }
    public void setTeamIds(List<Long> teamIds) { this.teamIds = teamIds; }

    public List<TeamDto> getTeams() { return teams; }
    public void setTeams(List<TeamDto> teams) { this.teams = teams; }
}
