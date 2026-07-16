package com.pedrocf01.blog_api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID uuid,
    String username,
    String email,
    String fullName,
    String bio,
    Instant createdAt)
{}
