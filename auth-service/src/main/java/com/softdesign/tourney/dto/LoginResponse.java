package com.softdesign.tourney.dto;

import java.util.List;

public class LoginResponse {
    private String token;
    private String username;
    private List<String> roles;
    private Long managedTeamId;

    public LoginResponse(String token, String username, List<String> roles, Long managedTeamId) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.managedTeamId = managedTeamId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public List<String> getRoles() { return roles; }
    public Long getManagedTeamId() { return managedTeamId; }
}
