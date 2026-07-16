package com.pedrocf01.blog_api.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.pedrocf01.blog_api.dto.request.LoginRequest;
import com.pedrocf01.blog_api.dto.response.AuthResponse;
import com.pedrocf01.blog_api.dto.response.UserResponse;
import com.pedrocf01.blog_api.entity.User;
import com.pedrocf01.blog_api.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }
    
    public AuthResponse login(LoginRequest request) {
        // Allow login with username or email
        String usernameOrEmail = request.username();
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());

        return new AuthResponse(toUserResponse(user), roles);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getUuid(), 
                                user.getUsername(), 
                                user.getEmail(), 
                                user.getFullName(), 
                                user.getBio(), 
                                user.getCreatedAt());
    }

}
