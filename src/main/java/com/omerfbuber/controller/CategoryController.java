package com.omerfbuber.controller;

import com.omerfbuber.dto.category.CategoryResponse;
import com.omerfbuber.dto.category.CreateCategoryRequest;
import com.omerfbuber.dto.category.UpdateCategoryRequest;
import com.omerfbuber.extension.ResponseEntityExtension;
import com.omerfbuber.service.category.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntityExtension.okOrProblem(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable int id) {
        return ResponseEntityExtension.okOrProblem(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CreateCategoryRequest request) {
        return ResponseEntityExtension.createdOrProblem(categoryService.save(request));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateCategoryRequest request) {
        return ResponseEntityExtension.noContentOrProblem(categoryService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return ResponseEntityExtension.noContentOrProblem(categoryService.delete(id));
    }
}
