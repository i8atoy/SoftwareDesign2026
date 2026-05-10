package com.softdesign.tourney.model; // Verify if this should be .model or .models

import jakarta.persistence.*;
import lombok.Builder;

@Builder
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;
    private int age;
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


    public Player() {
    }

    public Player(Long id, String name, String position, int age, String photoUrl, Team team) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.age = age;
        this.photoUrl = photoUrl;
        this.team = team;
    }

    // --- MANUALLY ADDED GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}