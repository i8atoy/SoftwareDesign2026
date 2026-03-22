package com.softdesign.tourney.dto;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class RegistrationDto {
    private Long id;
    @NotEmpty(message = "Username must not be empty")
    private String userName;
    @NotEmpty(message = "Password must not be empty")
    private String password;

}
