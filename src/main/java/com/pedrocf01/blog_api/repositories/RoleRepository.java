package com.pedrocf01.blog_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Short> {
    
}
