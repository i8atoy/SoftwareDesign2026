package com.softdesign.tourney.controller;

import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal REST API consumed by tournament-service and team-service.
 * Not exposed to the browser directly.
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}/teamId")
    public ResponseEntity<Long> getTeamId(@PathVariable String username) {
        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user.getManagedTeamId());
    }
}
