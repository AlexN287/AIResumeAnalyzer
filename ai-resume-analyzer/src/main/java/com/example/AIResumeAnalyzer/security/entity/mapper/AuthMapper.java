package com.example.AIResumeAnalyzer.security.entity.mapper;

import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.security.entity.AuthRequest;

public class AuthMapper {
    public static User toUser(AuthRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        return user;
    }
}
