package com.example.AIResumeAnalyzer.security.entity;

import com.example.AIResumeAnalyzer.dto.UserDTO;

public record AuthResponse(String token, UserDTO userDTO) {
}
