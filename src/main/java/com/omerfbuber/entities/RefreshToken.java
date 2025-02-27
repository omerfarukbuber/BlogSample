package com.omerfbuber.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens",
        indexes = {@Index(name = "idx_refresh_token_user_id", columnList = "user_id"),
                    @Index(name = "idx_refresh_token_token", columnList = "token")})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "client_info")
    private String clientInfo;
}
