package com.softdesign.tourney.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Typed client for auth-service REST API.
 * All cross-service HTTP calls from tournament-service → auth-service go here.
 */
@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceClient(RestTemplate restTemplate,
                             @Value("${auth.service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * Returns the team ID managed by this user, or null if they have none
     * (e.g. ADMIN or plain USER role).
     */
    public Long getTeamIdForUser(String username) {
        String url = authServiceUrl + "/api/users/" + username + "/teamId";
        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
