package com.example.cartservice.services;

import com.example.cartservice.clients.ProductServiceClient;
import com.example.cartservice.dtos.AddToCartRequestDto;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exceptions.CartNotFoundException;
import com.example.cartservice.exceptions.InvalidQuantityException;
import com.example.cartservice.exceptions.ProductNotInCartException;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.repos.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CartServiceImpl implements CartService {

    private static final String CACHE_KEY_PREFIX = "CART:";

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Override
    public Cart getCart(Long userId) {
        Cart cached = (Cart) redisTemplate.opsForValue().get(cacheKey(userId));
        if (cached != null) {
            return cached;
        }
        Cart cart = cartRepo.findByUserId(userId).orElseGet(() -> newCart(userId));
        redisTemplate.opsForValue().set(cacheKey(userId), cart);
        return cart;
    }

    @Override
    public Cart addItem(Long userId, AddToCartRequestDto requestDto) {
        if (requestDto.getProductId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        if (requestDto.getQuantity() == null || requestDto.getQuantity() <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }
        // get product details from Product Catalog Service to ensure price and name are correct
        ProductDto product = productServiceClient.getProductById(requestDto.getProductId());

        // find or create cart for user
        Cart cart = cartRepo.findByUserId(userId).orElseGet(() -> newCart(userId));
        
        // check if item already exists in cart, if so, update quantity, else add new item
        CartItem existingItem = findItem(cart, requestDto.getProductId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + requestDto.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(requestDto.getQuantity());
            cart.getItems().add(item);
        }
        return persist(cart);
    }

    @Override
    public Cart updateItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new ProductNotInCartException("Product not in cart: " + productId);
        }
        item.setQuantity(quantity);
        return persist(cart);
    }

    @Override
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new ProductNotInCartException("Product not in cart: " + productId);
        }
        return persist(cart);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepo.deleteByUserId(userId);
        redisTemplate.delete(cacheKey(userId));
    }

    private Cart persist(Cart cart) {
        recomputeTotals(cart);
        cart.setUpdatedAt(new Date());
        Cart saved = cartRepo.save(cart);
        redisTemplate.opsForValue().set(cacheKey(saved.getUserId()), saved);
        return saved;
    }

    private void recomputeTotals(Cart cart) {
        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            double subtotal = item.getPrice() * item.getQuantity();
            item.setSubtotal(subtotal);
            total += subtotal;
        }
        cart.setTotalPrice(total);
    }

    private CartItem findItem(Cart cart, Long productId) {
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private Cart newCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCreatedAt(new Date());
        cart.setUpdatedAt(new Date());
        return cart;
    }

    private String cacheKey(Long userId) {
        return CACHE_KEY_PREFIX + userId;
    }
}
