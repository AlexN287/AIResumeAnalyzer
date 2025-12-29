package com.example.AIResumeAnalyzer.security.controller;

import com.example.AIResumeAnalyzer.dto.UserDTO;
import com.example.AIResumeAnalyzer.mapper.UserMapper;
import com.example.AIResumeAnalyzer.model.User;
import com.example.AIResumeAnalyzer.security.JwtProvider;
import com.example.AIResumeAnalyzer.security.entity.AuthRequest;
import com.example.AIResumeAnalyzer.security.entity.AuthResponse;
import com.example.AIResumeAnalyzer.security.service.AuthService;
import com.example.AIResumeAnalyzer.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authenticationService;
    private final UserService userService;

    public AuthController(AuthService authenticationService, UserService userService)
    {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public AuthResponse registerUser(@RequestBody AuthRequest authRequest) throws Exception {
        UserDTO userDTO = authenticationService.registerUser(authRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, userDTO);

        return authResponse;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequest authRequest)
    {
        try{
            Authentication authentication = authenticationService.authenticate(authRequest.username(), authRequest.password());

            String token = JwtProvider.generateToken(authentication);

            User user = userService.getUserByUsername(authRequest.username());

            AuthResponse authResponse = new AuthResponse(token, UserMapper.toDTO(user));

            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body("You have been logged out successfully.");
    }
}