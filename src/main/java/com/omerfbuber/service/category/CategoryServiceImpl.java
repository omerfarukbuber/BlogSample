package com.omerfbuber.service.category;

import com.omerfbuber.dto.category.CategoryResponse;
import com.omerfbuber.dto.category.CreateCategoryRequest;
import com.omerfbuber.dto.category.UpdateCategoryRequest;
import com.omerfbuber.entity.Category;
import com.omerfbuber.repository.CategoryRepository;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Result<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = CategoryResponse.of(categoryRepository.findAll());
        return Result.success(categories);
    }

    @Override
    public Result<CategoryResponse> getById(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            log.info("Category with id {} not found when fetching.", id);
            return Result.failure(Error.notFound("Category.NotFound", "Category could not be found."));
        }
        return Result.success(CategoryResponse.of(category));
    }

    @Override
    public Result<CategoryResponse> save(CreateCategoryRequest createCategoryRequest) {
        if (categoryRepository.existsByName(createCategoryRequest.name())){
            log.info("Category with name {} already exists when saving.", createCategoryRequest.name());
            return Result.failure(Error.conflict("Category.AlreadyExists", "Category already exists."));
        }
        Category category = new Category();
        category.setName(createCategoryRequest.name());
        Category newCategory = categoryRepository.save(category);
        return Result.created(CategoryResponse.of(newCategory), "api/categories/" + newCategory.getId());
    }

    @Override
    public Result<Void> update(UpdateCategoryRequest updateCategoryRequest) {
        Category entity = categoryRepository.findById(updateCategoryRequest.id()).orElse(null);
        if (entity == null) {
            log.info("Category with id {} not found when updating.", updateCategoryRequest.id());
            return Result.failure(Error.notFound("Category.NotFound", "Category could not be found."));
        }

        if (categoryRepository.existsByNameAndIdNot(updateCategoryRequest.name(), updateCategoryRequest.id())) {
            log.info("Category with name {} already exists when updating.", updateCategoryRequest.name());
            return Result.failure(Error.conflict("Category.AlreadyExists", "Category already exists."));
        }

        entity.setName(updateCategoryRequest.name());
        categoryRepository.save(entity);
        return Result.success();
    }

    @Override
    public Result<Void> delete(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            log.info("Category with id {} not found when deleting.", id);
            return Result.failure(Error.notFound("Category.NotFound", "Category could not be found."));
        }
        categoryRepository.delete(category);
        return Result.success();
    }
}
