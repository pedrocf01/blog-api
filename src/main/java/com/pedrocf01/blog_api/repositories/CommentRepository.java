package com.pedrocf01.blog_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    
}
