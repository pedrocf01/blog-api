package com.pedrocf01.blog_api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.pedrocf01.blog_api.dto.request.RegisterRequest;
import com.pedrocf01.blog_api.dto.response.AuthResponse;
import com.pedrocf01.blog_api.dto.response.UserResponse;
import com.pedrocf01.blog_api.exception.DuplicateResourceException;
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

    @Nested
    @DisplayName("POST /auth/register")
    class Register {

        private RegisterRequest validRequest() {
            RegisterRequest request = new RegisterRequest("pedro", "pedro@example.com",
                                "Passw0rd", "Pedro C Ferreira");

            return request;
        }

        @Test
        @DisplayName("returns 201 and the auth payload on success")
        void register_validRequest_returnsCreated() throws Exception {
            when(authService.register(any(RegisterRequest.class))).thenReturn(sampleAuthResponse);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value("Registration successful"))
                        .andExpect(jsonPath("$.data.user.username").value("pedro"));

            verify(authService).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("returns 400 with field errors for an invalid email")
        void register_invalidEmail_returnsBadRequest() throws Exception {
            RegisterRequest request = validRequest();
            request = request.withEmail("not-an-email");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("returns 400 when password fails the complexity pattern")
        void register_weakPassword_returnsBadRequest() throws Exception {
            RegisterRequest request = validRequest();
            request = request.withPassword("alllowercase1");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.errors.password").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("returns 400 when username contains invalid characters")
        void register_invalidUsernameCharacters_returnsBadRequest() throws Exception {
            RegisterRequest request = validRequest();
            request = request.withUsername("jdoe!!!");

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("returns 409 when the service reports a duplicate username/email")
        void register_duplicateUser_returnsConflict() throws Exception {
            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new DuplicateResourceException("Username already taken"));

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Username already taken"));
        }
    }
}
