package com.pedrocf01.blog_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedrocf01.blog_api.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findByName(Role.RoleName name);
}
