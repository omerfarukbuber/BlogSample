package com.omerfbuber.controller;

import com.omerfbuber.dto.article.ArticleResponse;
import com.omerfbuber.dto.article.CreateArticleRequest;
import com.omerfbuber.extension.ResponseEntityExtension;
import com.omerfbuber.service.article.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable long id) {
        return ResponseEntityExtension.okOrProblem(articleService.getArticle(id));
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> create(@RequestBody CreateArticleRequest request) {
        return ResponseEntityExtension.okOrProblem(articleService.save(request));
    }

}
