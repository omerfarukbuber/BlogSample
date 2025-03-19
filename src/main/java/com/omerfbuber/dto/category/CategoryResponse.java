package com.omerfbuber.dto.category;

import com.omerfbuber.entity.Category;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public record CategoryResponse(int id, String name) implements Serializable {
    public static CategoryResponse of(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName());
    }

    public static List<CategoryResponse> of(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }
}
