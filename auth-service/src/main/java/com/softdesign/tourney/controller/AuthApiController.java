package com.softdesign.tourney.controller;

import com.softdesign.tourney.dto.LoginRequest;
import com.softdesign.tourney.dto.LoginResponse;
import com.softdesign.tourney.dto.RegistrationDto;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.service.UserService;
import com.softdesign.tourney.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtUtil jwtUtil,
                             UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            List<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(request.getUsername(), roles);

            UserEntity user = userService.findByUsername(request.getUsername());
            Long managedTeamId = user != null ? user.getManagedTeamId() : null;

            return ResponseEntity.ok(new LoginResponse(token, request.getUsername(), roles, managedTeamId));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDto dto) {
        if (userService.findByUsername(dto.getUserName()) != null) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        userService.saveUser(dto);
        return ResponseEntity.ok("User registered successfully");
    }
}
