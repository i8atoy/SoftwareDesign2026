package com.softdesign.tourney.service;

import com.softdesign.tourney.dto.RegistrationDto;
import com.softdesign.tourney.models.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;


@Service
public interface UserService {
    void saveUser(RegistrationDto registrationDto);

    UserEntity findByUsername(@NotEmpty(message = "Username must not be empty") String userName);
}
