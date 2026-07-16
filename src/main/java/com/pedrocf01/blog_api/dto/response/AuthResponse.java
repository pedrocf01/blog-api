package com.pedrocf01.blog_api.dto.response;

import java.util.Set;

public record AuthResponse(UserResponse user, Set<String> roles) {}
