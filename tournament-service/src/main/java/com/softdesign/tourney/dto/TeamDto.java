package com.softdesign.tourney.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Mirror of team-service's TeamDto.
 * Only the fields tournament-service needs for the tournaments-list view.
 */
public class TeamDto {
    private Long id;
    private String name;
    private String country;
    private int vrsPoints;
    private String photoUrl;
    private List<PlayerDto> players = new ArrayList<>();

    public List<PlayerDto> getPlayers() { return players; }
    public void setPlayers(List<PlayerDto> players) { this.players = players; }

    public TeamDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public int getVrsPoints() { return vrsPoints; }
    public void setVrsPoints(int vrsPoints) { this.vrsPoints = vrsPoints; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
