package com.pedrocf01.blog_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    
}
