package com.priyasingh.ecommerce.service;

import com.priyasingh.ecommerce.exceptions.APIException;
import com.priyasingh.ecommerce.exceptions.ResourseNotFoundException;
import com.priyasingh.ecommerce.model.Cart;
import com.priyasingh.ecommerce.model.CartItem;
import com.priyasingh.ecommerce.model.Product;
import com.priyasingh.ecommerce.payload.CartDTO;
import com.priyasingh.ecommerce.payload.ProductDTO;
import com.priyasingh.ecommerce.repository.CartItemRepository;
import com.priyasingh.ecommerce.repository.CartRepository;
import com.priyasingh.ecommerce.repository.ProductRepository;
import com.priyasingh.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class CartServiceImplementation implements CartService {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // Find existing cart associated with the user or create one
        Cart cart = createCart();

        // Retrive product details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productId));

        // Perform validations
        // 1. Check if this particular product exist in loggedin user cart item
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );
        if(cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        // 2. Product quantity us zero in database
        if(product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available.");
        }

        // 3. requested quantity is more than available
        if(product.getQuantity() < quantity) {
            throw new APIException("Please make an order of " + product.getProductName() + " less than or equal to quantity: " + product.getQuantity());
        }

        // Create Cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // maintain both sides of relationship
        cart.getCartItems().add(newCartItem);

        // Saved cart item
        cartItemRepository.save(newCartItem);

        // update the cart related field in db
        product.setQuantity(product.getQuantity()); // can reduce when added to cart or can reduce when order placed
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // Return Updated cart
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // explicitly map all product dto and set quantity from cartitem
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity()); // if not done explicitly then the total product quantity will be added here instead of the cartItem product quantity
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()) {
            throw new APIException("No carts found");
        }

        List<CartDTO> cartDTOs = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(cartItem -> {
                                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                                productDTO.setQuantity(cartItem.getQuantity());
                                return productDTO;
                            }).toList();

                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();

        return cartDTOs;
    }

    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);

         if(cart == null) {
            throw new ResourseNotFoundException("Cart", "cartId", cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(cartItem ->
                cartItem.getProduct().setQuantity(cartItem.getQuantity()));

        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(cartItem -> {
                    return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                }).toList();

        cartDTO.setProducts(productDTOS);

        return modelMapper.map(cart, CartDTO.class);
    }

    @Override
    @Transactional // multiple operations are done in this method, so this annotation ensures that either all operations are performed or if there is error in any one then the entire thing is rolled back
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourseNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productId));

        if(product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available.");
        }

        if(product.getQuantity() < quantity) {
            throw new APIException("Please make an order of " + product.getProductName() + " less than or equal to quantity: " + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null) {
            throw new APIException("Product" + product.getProductName() + " is not available.");
        }

        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(product.getQuantity() + quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);
        CartItem updatedItem = cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(updatedItem, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourseNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null) {
            throw new ResourseNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        cartRepository.save(cart);
        return "Product " + cartItem.getProduct().getProductName() + " has been deleted !!!";
    }
}
