package com.priyasingh.ecommerce.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// representing an individual order in our app
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO productDTO;
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
}
