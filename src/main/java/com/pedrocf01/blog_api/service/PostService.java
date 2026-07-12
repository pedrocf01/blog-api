package com.pedrocf01.blog_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pedrocf01.blog_api.dto.response.PagedResponse;
import com.pedrocf01.blog_api.dto.response.PostSummaryResponse;
import com.pedrocf01.blog_api.entity.Post;
import com.pedrocf01.blog_api.entity.Post.PostStatus;
import com.pedrocf01.blog_api.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<PostSummaryResponse> getAllPublishedPosts(Pageable pageable) {
        Page<Post> page = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
        return PagedResponse.of(page.map(this::toSummary));
    }

    private PostSummaryResponse toSummary(Post post) {
        return new PostSummaryResponse(
                        post.getUuid(),
                        post.getTitle(),
                        post.getSlug(),
                        post.getExcerpt(),
                        post.getCoverImage(),
                        post.getStatus(),
                        post.getViewsCount(),
                        post.getLikesCount(),
                        post.getComments().size(),
                        post.getAuthor().getUsername(),
                        post.getPublishedAt(),
                        post.getCreatedAt()
                    );
                
    }
}
