package com.example.AIResumeAnalyzer.mapper;

import com.example.AIResumeAnalyzer.dto.UserDTO;
import com.example.AIResumeAnalyzer.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername());
    }
}
