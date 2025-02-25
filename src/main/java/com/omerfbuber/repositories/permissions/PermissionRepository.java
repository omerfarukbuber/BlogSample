package com.omerfbuber.repositories.permissions;

import com.omerfbuber.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Optional<List<Permission>> findByNameIn(List<String> names);
}
