package com.softdesign.tourney.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<TeamDto> teams;
}
