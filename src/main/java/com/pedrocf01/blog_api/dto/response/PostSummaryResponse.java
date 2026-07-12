package com.pedrocf01.blog_api.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.pedrocf01.blog_api.entity.Post.PostStatus;

public record PostSummaryResponse(
    UUID uuid,
    String title,
    String slug,
    String excerpt,
    String coverImage,
    PostStatus status,
    long viewsCount,
    long likesCount,
    long commentsCount,
    String authorUsername,
    Instant publishedAt,
    Instant createdAt
){}
