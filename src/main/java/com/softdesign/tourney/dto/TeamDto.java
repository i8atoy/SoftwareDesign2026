package com.softdesign.tourney.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamDto{
    private Long id;
    private String name;
    private String country;
    private int vrsPoints;
    private String photoUrl;

    private List<PlayerDto> players;
}
