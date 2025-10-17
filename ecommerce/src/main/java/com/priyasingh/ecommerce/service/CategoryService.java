package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.model.Category;
import com.priyasingh.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    Category updateCategory(Long categoryId, Category category);
}
