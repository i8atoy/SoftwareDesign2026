package com.softdesign.tourney.client;

import com.softdesign.tourney.dto.TeamDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Typed client for team-service REST API.
 * Used to resolve team IDs stored in Tournament into full TeamDto objects for display.
 */
@Component
public class TeamServiceClient {

    private final RestTemplate restTemplate;
    private final String teamServiceUrl;

    public TeamServiceClient(RestTemplate restTemplate,
                             @Value("${team.service.url}") String teamServiceUrl) {
        this.restTemplate = restTemplate;
        this.teamServiceUrl = teamServiceUrl;
    }

    public TeamDto getTeamById(Long teamId) {
        String url = teamServiceUrl + "/api/teams/" + teamId;
        try {
            ResponseEntity<TeamDto> response = restTemplate.getForEntity(url, TeamDto.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
