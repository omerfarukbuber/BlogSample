package com.omerfbuber.service.articlelike;

import com.omerfbuber.dto.articlelike.DislikeRequest;
import com.omerfbuber.dto.articlelike.LikeRequest;
import com.omerfbuber.dto.articlelike.LikeResponse;
import com.omerfbuber.result.Result;

public interface ArticleLikeService {
    Result<LikeResponse> likeArticle(LikeRequest request);
    Result<LikeResponse> dislikeArticle(DislikeRequest request);
    Result<LikeResponse> getLikeCount(long articleId);
}
