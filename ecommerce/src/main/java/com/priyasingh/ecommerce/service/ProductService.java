package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.model.Product;
import com.priyasingh.ecommerce.payload.ProductDTO;
import com.priyasingh.ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);
    ProductResponse getAllProducts();
    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

}
