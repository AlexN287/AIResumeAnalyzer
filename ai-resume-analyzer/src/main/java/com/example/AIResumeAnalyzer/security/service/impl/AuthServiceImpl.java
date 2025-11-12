package com.example.AIResumeAnalyzer.security.service.impl;

import com.example.AIResumeAnalyzer.dto.UserDTO;
import com.example.AIResumeAnalyzer.mapper.UserMapper;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.repository.UserRepository;
import com.example.AIResumeAnalyzer.security.UserDetailsServiceImpl;
import com.example.AIResumeAnalyzer.security.entity.AuthRequest;
import com.example.AIResumeAnalyzer.security.entity.mapper.AuthMapper;
import com.example.AIResumeAnalyzer.security.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashSet;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(String username, String password)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(userDetails==null)
        {
            throw new BadCredentialsException("Invalid username");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword()))
        {
            throw new BadCredentialsException("Password incorrect");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Transactional
    @Override
    public UserDTO registerUser(AuthRequest authRequest) throws Exception {
        User isExist = userRepository.getUserByUsername(authRequest.username());

        if(isExist!=null)
        {
            throw new Exception("Username already exists");
        }

        User user = AuthMapper.toUser(authRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);
    }

}
