package com.example.AIResumeAnalyzer.service.implementations;

import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }
}
