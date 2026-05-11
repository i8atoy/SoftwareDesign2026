package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.dto.RegistrationDto;
import com.softdesign.tourney.models.Role;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.repository.RoleRepository;
import com.softdesign.tourney.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImplementation userService;

    // ── saveUser (command) ────────────────────────────────────────────────────

    @Test
    void saveUser_encodesPasswordAndAssignsUserRole() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUserName("john");
        dto.setPassword("plaintext");

        Role userRole = new Role();
        userRole.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.saveUser(dto);

        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(argThat(user ->
                user.getUserName().equals("john") &&
                user.getPassword().equals("encoded_password") &&
                user.getRoles().size() == 1 &&
                user.getRoles().get(0).getName().equals("USER")
        ));
    }

    @Test
    void saveUser_persistsToRepository() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUserName("jane");
        dto.setPassword("secret");

        Role userRole = new Role();
        userRole.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.saveUser(dto);

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void saveUser_neverStoresPlaintextPassword() {
        RegistrationDto dto = new RegistrationDto();
        dto.setUserName("bob");
        dto.setPassword("mypassword");

        Role userRole = new Role();
        userRole.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(passwordEncoder.encode("mypassword")).thenReturn("$2a$hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.saveUser(dto);

        verify(userRepository).save(argThat(user ->
                !user.getPassword().equals("mypassword")
        ));
    }

    // ── findByUsername (query) ────────────────────────────────────────────────

    @Test
    void findByUsername_returnsUserWhenFound() {
        UserEntity user = new UserEntity();
        user.setUserName("john");
        user.setPassword("hashed");

        when(userRepository.findUserByUserName("john")).thenReturn(user);

        UserEntity result = userService.findByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUserName());
    }

    @Test
    void findByUsername_returnsNullWhenNotFound() {
        when(userRepository.findUserByUserName("nobody")).thenReturn(null);

        UserEntity result = userService.findByUsername("nobody");

        assertNull(result);
    }

    @Test
    void findByUsername_delegatesToRepository() {
        when(userRepository.findUserByUserName("john")).thenReturn(null);

        userService.findByUsername("john");

        verify(userRepository, times(1)).findUserByUserName("john");
    }
}