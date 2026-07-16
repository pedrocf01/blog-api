package com.pedrocf01.blog_api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrocf01.blog_api.controller.AuthController;
import com.pedrocf01.blog_api.dto.request.LoginRequest;
import com.pedrocf01.blog_api.dto.response.AuthResponse;
import com.pedrocf01.blog_api.dto.response.UserResponse;
import com.pedrocf01.blog_api.service.AuthService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    private AuthResponse sampleAuthResponse;

    @BeforeEach
    void setUp() {
        UserResponse user = new UserResponse(UUID.randomUUID(), "pedro", "pedro@example.com", "Pedro C Ferreira", "Meu nome é Pedro", Instant.now());

        sampleAuthResponse = new AuthResponse(user, Set.of("ROLE_USER"));
    }
    
    
    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("returns 200 and the auth payload for valid credentials")
        void login_validCredentials_returnsOk() throws Exception {
            LoginRequest request = new LoginRequest("pedro", "password");
            
            when(authService.login(any(LoginRequest.class))).thenReturn(sampleAuthResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.roles[0]").value("ROLE_USER"));
        }

        @Test
        @DisplayName("returns 401 when credentials are invalid")
        void login_badCredentials_returnsUnauthorized() throws Exception {
            LoginRequest request = new LoginRequest("pedro","wrongPassword");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Username ou senha inválidos"));
        }
    }
}
