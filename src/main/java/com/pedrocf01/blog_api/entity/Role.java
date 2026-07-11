package com.pedrocf01.blog_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    public Role() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_AUTHOR,
        ROLE_ADMIN
    }
}
