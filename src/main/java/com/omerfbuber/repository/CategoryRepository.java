package com.omerfbuber.repository;

import com.omerfbuber.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
}
