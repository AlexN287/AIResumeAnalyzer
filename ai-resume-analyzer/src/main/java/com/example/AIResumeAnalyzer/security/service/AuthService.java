package com.example.AIResumeAnalyzer.security.service;

import com.example.AIResumeAnalyzer.dto.UserDTO;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.security.entity.AuthRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    Authentication authenticate(String username, String password);
    UserDTO registerUser(AuthRequest authRequest) throws Exception;
}
