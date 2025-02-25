package com.omerfbuber.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "permissions", indexes = {@Index(name = "idx_permission_name", columnList = "name")})
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }
}
