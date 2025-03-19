package com.omerfbuber.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "roles", schema = "users", indexes = {@Index(name = "idx_role_name", columnList = "name")})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    public Role() {
    }

    public Role(String name){
        this.name = name;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Role getAdminRole()
    {
        return new Role(1L, "ADMIN");
    }

    public static Role getUserRole()
    {
        return new Role(2L, "USER");
    }

    public static Role getEmptyRole() {
        return new Role (3L, "EMPTY");
    }


}
