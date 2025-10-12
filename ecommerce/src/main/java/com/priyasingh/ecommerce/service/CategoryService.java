package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.model.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Long categoryId, Category category);
}
