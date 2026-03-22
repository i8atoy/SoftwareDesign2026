package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.RegistrationDto;
import com.softdesign.tourney.models.Role;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.repository.RoleRepository;
import com.softdesign.tourney.repository.UserRepository;
import com.softdesign.tourney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImplementation implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(RegistrationDto registrationDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(registrationDto.getUserName());

        userEntity.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        Role role = roleRepository.findByName("USER");
        userEntity.setRoles(Arrays.asList(role));
        userRepository.save(userEntity);
    }

    @Override
    public UserEntity findByUsername(String userName) {
        return userRepository.findUserByUserName(userName);
    }
}