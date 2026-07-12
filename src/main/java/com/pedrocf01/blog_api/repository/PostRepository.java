package com.pedrocf01.blog_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Post;
import com.pedrocf01.blog_api.entity.Post.PostStatus;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByStatus(PostStatus published, Pageable pageable);
    
}
