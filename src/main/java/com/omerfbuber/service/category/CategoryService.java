package com.omerfbuber.service.category;

import com.omerfbuber.dto.category.CategoryResponse;
import com.omerfbuber.dto.category.CreateCategoryRequest;
import com.omerfbuber.dto.category.UpdateCategoryRequest;
import com.omerfbuber.result.Result;

import java.util.List;

public interface CategoryService {
    Result<List<CategoryResponse>> getAllCategories();
    Result<CategoryResponse> getById(int id);
    Result<CategoryResponse> save(CreateCategoryRequest createCategoryRequest);
    Result<Void> update(UpdateCategoryRequest updateCategoryRequest);
    Result<Void> delete(int id);
}
