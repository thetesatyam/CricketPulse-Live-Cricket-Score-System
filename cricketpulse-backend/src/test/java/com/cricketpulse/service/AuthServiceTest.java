package com.cricketpulse.service;

import com.cricketpulse.config.JwtTokenProvider;
import com.cricketpulse.dto.AuthRequest;
import com.cricketpulse.dto.AuthResponse;
import com.cricketpulse.dto.RegisterRequest;
import com.cricketpulse.dto.UserDto;
import com.cricketpulse.entity.Role;
import com.cricketpulse.entity.User;
import com.cricketpulse.exception.BadRequestException;
import com.cricketpulse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("cricketfan", "fan@cricketpulse.io", "$2a$10$encodedHash", Role.ROLE_VIEWER);
        sampleUser.setId(1L);
    }

    @Test
    @DisplayName("Public registration assigns ROLE_VIEWER and hashes password")
    void testRegistrationAssignsViewerRole() {
        RegisterRequest req = new RegisterRequest("newuser", "new@cricketpulse.io", "secret123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@cricketpulse.io")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserDto dto = userService.register(req);

        assertNotNull(dto);
        assertEquals("newuser", dto.getUsername());
        assertEquals(Role.ROLE_VIEWER, dto.getRole());
        verify(passwordEncoder).encode("secret123");
        verify(auditService).log(eq("USER_REGISTER"), eq("User"), any(), any());
    }

    @Test
    @DisplayName("Login with valid credentials returns JWT token and user info")
    void testLoginSuccess() {
        AuthRequest req = new AuthRequest("cricketfan", "password123");

        when(userRepository.findByUsername("cricketfan")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("password123", "$2a$10$encodedHash")).thenReturn(true);
        when(tokenProvider.generateToken("cricketfan", Role.ROLE_VIEWER)).thenReturn("mock.jwt.token");

        AuthResponse res = userService.authenticate(req);

        assertNotNull(res);
        assertEquals("mock.jwt.token", res.getToken());
        assertEquals("cricketfan", res.getUsername());
        assertEquals(Role.ROLE_VIEWER, res.getRole());
    }

    @Test
    @DisplayName("Login with invalid password throws BadRequestException")
    void testLoginInvalidPassword() {
        AuthRequest req = new AuthRequest("cricketfan", "wrongpassword");

        when(userRepository.findByUsername("cricketfan")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedHash")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.authenticate(req));
    }
}
