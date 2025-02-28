package com.omerfbuber.extensions;

import com.omerfbuber.entities.Permission;
import com.omerfbuber.entities.Role;
import com.omerfbuber.repositories.permissions.PermissionRepository;
import com.omerfbuber.repositories.roles.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public DataSeeder(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedPermissions();
        seedRoles();
    }

    private void seedPermissions() {
        List<String> permissions = Arrays.asList("User.Read", "User.Update", "User.Delete.Any", "User.Delete.Self");

        for (String permissionName : permissions) {
            permissionRepository.findByName(permissionName).orElseGet(
                    () -> permissionRepository.save(new Permission(permissionName)));
        }
    }

    private void seedRoles() {
        Role admin = roleRepository.findByName("Admin").orElseGet(
                () -> roleRepository.save(new Role("Admin")));

        Role user = roleRepository.findByName("User").orElseGet(
                () -> roleRepository.save(new Role("User")));

        Role empty = roleRepository.findByName("Empty").orElseGet(
                () -> roleRepository.save(new Role("Empty")));

        Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
        Set<Permission> userPermissions = new HashSet<>(permissionRepository.findByNameIn(
                Arrays.asList("User.Read", "User.Delete.Self")).orElse(Collections.emptyList()));

        boolean adminNeedsUpdate = !admin.getPermissions().equals(allPermissions);
        boolean userNeedsUpdate = !user.getPermissions().equals(userPermissions);

        if (adminNeedsUpdate) {
            admin.setPermissions(allPermissions);
            roleRepository.save(admin);
        }

        if (userNeedsUpdate) {
            user.setPermissions(userPermissions);
            roleRepository.save(user);
        }

        roleRepository.save(empty);
    }
}
