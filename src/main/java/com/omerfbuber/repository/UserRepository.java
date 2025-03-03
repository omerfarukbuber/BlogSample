package com.omerfbuber.repository;

import com.omerfbuber.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) FROM users.users u",
            nativeQuery = true)
    Optional<List<String>> getFullNameList();

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.password = :password WHERE u.email = :email",
            nativeQuery = false)
    int updateUserPassword(@Param("email") String email, @Param("password") String password);

    @Query(value = "SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.permissions p WHERE u.email = :email")
    Optional<User> findByEmailWithRoleAndPermissions(@Param("email") String email);

}
