package com.omerfbuber.repository;

import com.omerfbuber.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    int countByArticleId(Long articleId);
    Optional<ArticleLike> getByArticleIdAndUserId(Long articleId, Long userId);
    boolean existsByArticleIdAndUserId(Long articleId, Long userId);
    void deleteByArticleIdAndUserId(Long articleId, Long userId);

}
