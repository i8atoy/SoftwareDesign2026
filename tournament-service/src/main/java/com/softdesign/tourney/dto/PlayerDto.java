package com.softdesign.tourney.dto;

public class PlayerDto {
    private Long id;
    private String name;
    private String position;
    private int age;
    private String photoUrl;
    private TeamDto team;

    public PlayerDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public TeamDto getTeam() { return team; }
    public void setTeam(TeamDto team) { this.team = team; }
}