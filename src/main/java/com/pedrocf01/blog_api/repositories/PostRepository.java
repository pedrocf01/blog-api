package com.pedrocf01.blog_api.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
}
