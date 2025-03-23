package com.omerfbuber.repository;

import com.omerfbuber.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Set<Category> findCategoriesByIdIn(Set<Integer> ids);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
