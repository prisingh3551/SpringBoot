package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.exceptions.APIException;
import com.priyasingh.ecommerce.exceptions.ResourseNotFoundException;
import com.priyasingh.ecommerce.model.Category;
import com.priyasingh.ecommerce.model.Product;
import com.priyasingh.ecommerce.payload.ProductDTO;
import com.priyasingh.ecommerce.payload.ProductResponse;
import com.priyasingh.ecommerce.repository.CategoryRepository;
import com.priyasingh.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = category.getProducts();
        for(Product product : products){
            if(product.getProductName().equals(productDTO.getProductName())){
                throw new APIException("Product already exists!!");
            }
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = (1 - product.getDiscount() * 0.01) * product.getPrice();
        product.setSpecialPrice(specialPrice);
        Product savedProduct =  productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        if(allProducts.isEmpty()) {
            throw new APIException("No product added till now");
        }

        List<ProductDTO> productDTOs = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        if(products.isEmpty()) {
            throw new APIException("No product added till now");
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');

        if(products.isEmpty()) {
            throw new APIException("No product added till now");
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productDTO.getProductId()));

        Product product = modelMapper.map(productDTO, Product.class);

        existingProduct.setCategory(product.getCategory());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());

        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(existingProduct);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productId));

        productRepository.deleteById(productId);
        return modelMapper.map(existingProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from DB
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productId));

        // Upload image to server
        // get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        // updating the new file name to the product
        existingProduct.setImage(fileName);

        // save product to db
        productRepository.save(existingProduct);

        // return DTO after mapping product to DTO
        return modelMapper.map(existingProduct, ProductDTO.class);
    }

}
