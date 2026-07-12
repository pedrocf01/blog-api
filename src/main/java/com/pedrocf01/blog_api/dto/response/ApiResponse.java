package com.pedrocf01.blog_api.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T> (
    boolean success,
    String message,
    T data,
    Instant timestamp 
){
    
    public ApiResponse(boolean success, String message, T data){
        this(success, message, data, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(true, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<T>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(false, message, null);
    }
}
