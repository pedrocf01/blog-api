package com.pedrocf01.blog_api.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pedrocf01.blog_api.dto.response.ApiResponse;
import com.pedrocf01.blog_api.dto.response.PagedResponse;
import com.pedrocf01.blog_api.dto.response.PostSummaryResponse;
import com.pedrocf01.blog_api.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
            this.postService = postService;
        }

    // ==================== PUBLIC ENDPOINTS ====================

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PostSummaryResponse>>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
        return ResponseEntity.ok(ApiResponse.ok(postService.getAllPublishedPosts(pageable)));
    }
}
