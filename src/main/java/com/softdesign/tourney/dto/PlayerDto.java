package com.softdesign.tourney.dto;

import com.softdesign.tourney.models.Team;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PlayerDto {
    private Long id;

    private String name;
    private String position;
    private int age;
    private Team team;
    private String photoUrl;
}
