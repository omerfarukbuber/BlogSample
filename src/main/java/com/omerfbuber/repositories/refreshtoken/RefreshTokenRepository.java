package com.omerfbuber.repositories.refreshtoken;

import com.omerfbuber.entities.RefreshToken;
import com.omerfbuber.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.clientInfo = :clientInfo")
    Optional<RefreshToken> findByUserAndClientInfo(@Param("user") User user, @Param("clientInfo") String clientInfo);
}
