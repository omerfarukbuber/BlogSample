package com.omerfbuber.services.refreshtoken;

import com.omerfbuber.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Slf4j
public class RefreshTokenCleanupService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    void deleteExpiredRefreshTokens() {
        var count = refreshTokenRepository.deleteByExpiryDateLessThan(new Date());
        log.info("Deleted {} expired refresh tokens", count);
    }
}
