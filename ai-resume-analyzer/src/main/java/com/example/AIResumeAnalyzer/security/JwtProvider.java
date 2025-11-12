package com.example.AIResumeAnalyzer.security;

import com.example.AIResumeAnalyzer.exceptions.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtProvider {

    public static String generateToken(Authentication auth)
    {
        String jwt = Jwts.builder()
                .setIssuer("AIResumeAnalyzerApp").setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", auth.getName())
                .signWith(JwtKeyHolder.getKey())
                .compact();

        return jwt;
    }

    public static String getUsernameFromJwtToken(String jwt){
        jwt = jwt.substring(7);

        Claims claims = Jwts.parser().setSigningKey(JwtKeyHolder.getKey()).build().parseClaimsJws(jwt).getBody();

        String username = String.valueOf(claims.get("username"));

        return username;
    }

    public boolean validateToken(String token) {

        try {
            String username = getUsernameFromJwtToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException("JWT token is expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Invalid JWT token", HttpStatus.BAD_REQUEST);
        }
    }
}
