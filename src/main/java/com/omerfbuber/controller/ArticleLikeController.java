package com.omerfbuber.controller;

import com.omerfbuber.dto.articlelike.LikeRequest;
import com.omerfbuber.dto.articlelike.LikeResponse;
import com.omerfbuber.dto.articlelike.UnlikeRequest;
import com.omerfbuber.extension.ResponseEntityExtension;
import com.omerfbuber.service.articlelike.ArticleLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/articlelikes")
public class ArticleLikeController {
    private final ArticleLikeService articleLikeService;

    public ArticleLikeController(ArticleLikeService articleLikeService) {
        this.articleLikeService = articleLikeService;
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<LikeResponse> getCount(@PathVariable long id) {
        return ResponseEntityExtension.okOrProblem(articleLikeService.getLikeCount(id));
    }


    @PostMapping
    public ResponseEntity<LikeResponse> like(@RequestBody LikeRequest request) {
        return ResponseEntityExtension.okOrProblem(articleLikeService.likeArticle(request));
    }

    @DeleteMapping
    public ResponseEntity<LikeResponse> unlike(@RequestBody UnlikeRequest request) {
        return ResponseEntityExtension.okOrProblem(articleLikeService.unlikeArticle(request));
    }
}
