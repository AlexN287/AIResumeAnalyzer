package com.example.AIResumeAnalyzer.service;

import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.service.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Mock the repository (no DB)
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testGetUserByUsername_ReturnsUser() {
        User mockUser = new User("testuser", "password123");
        when(userRepository.getUserByUsername("testuser")).thenReturn(mockUser);

        User result = userService.getUserByUsername("testuser");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockUser, result);
    }

    @Test
    void testGetUserByUsername_ReturnsNullIfNotFound() {
        when(userRepository.getUserByUsername("missing")).thenReturn(null);

        User result = userService.getUserByUsername("missing");

        Assertions.assertNull(result);
    }
}
