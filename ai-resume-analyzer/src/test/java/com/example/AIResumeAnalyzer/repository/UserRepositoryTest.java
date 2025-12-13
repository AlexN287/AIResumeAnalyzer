package com.example.AIResumeAnalyzer.repository;

import com.example.AIResumeAnalyzer.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testGetUserByUsername() {
        User user = new User("testuser", "password123");
        userRepository.save(user);

        User found = userRepository.getUserByUsername("testuser");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testuser");
        assertThat(found.getPassword()).isEqualTo("password123");
    }
}
