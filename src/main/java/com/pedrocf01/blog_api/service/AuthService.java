package com.pedrocf01.blog_api.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pedrocf01.blog_api.dto.request.LoginRequest;
import com.pedrocf01.blog_api.dto.request.RegisterRequest;
import com.pedrocf01.blog_api.dto.response.AuthResponse;
import com.pedrocf01.blog_api.dto.response.UserResponse;
import com.pedrocf01.blog_api.entity.Role;
import com.pedrocf01.blog_api.entity.User;
import com.pedrocf01.blog_api.exception.DuplicateResourceException;
import com.pedrocf01.blog_api.exception.ResourceNotFoundException;
import com.pedrocf01.blog_api.repository.RoleRepository;
import com.pedrocf01.blog_api.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    
    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username '%s' is already taken".formatted(request.username()));
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email '%s' is already registered".formatted(request.email()));
        }

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));

        User user = new User(UUID.randomUUID(), request.username(), request.email(), 
                            passwordEncoder.encode(request.password()), request.fullName(),
                            Set.of(userRole));

        userRepository.save(user);

        return buildAuthResponse(user);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Allow login with username or email
        String usernameOrEmail = request.username();
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.password()));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
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
