package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.model.Category;
import com.priyasingh.ecommerce.payload.CategoryDTO;
import com.priyasingh.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    String deleteCategory(Long categoryId);
    Category updateCategory(Long categoryId, Category category);
}
