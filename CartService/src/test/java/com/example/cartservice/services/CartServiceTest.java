package com.example.cartservice.services;

import com.example.cartservice.clients.ProductServiceClient;
import com.example.cartservice.dtos.AddToCartRequestDto;
import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exceptions.CartNotFoundException;
import com.example.cartservice.exceptions.InvalidQuantityException;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.repos.CartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(cartRepo.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getCart_returnsCachedCart_whenPresentInRedis() {
        Cart cachedCart = new Cart();
        cachedCart.setUserId(1L);
        when(valueOperations.get("CART:1")).thenReturn(cachedCart);

        Cart result = cartService.getCart(1L);

        assertEquals(cachedCart, result);
        verify(cartRepo, org.mockito.Mockito.never()).findByUserId(anyLong());
    }

    @Test
    void getCart_fetchesFromMongoAndCaches_whenNotInRedis() {
        Cart mongoCart = new Cart();
        mongoCart.setUserId(1L);
        when(valueOperations.get("CART:1")).thenReturn(null);
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(mongoCart));

        Cart result = cartService.getCart(1L);

        assertEquals(mongoCart, result);
        verify(valueOperations).set("CART:1", mongoCart);
    }

    @Test
    void addItem_fetchesProductFromCatalogAndAddsToNewCart() {
        ProductDto product = new ProductDto();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        when(productServiceClient.getProductById(10L)).thenReturn(product);
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.empty());

        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setProductId(10L);
        requestDto.setQuantity(2);

        Cart result = cartService.addItem(1L, requestDto);

        assertEquals(1, result.getItems().size());
        assertEquals(2000.0, result.getTotalPrice());
        assertEquals("Laptop", result.getItems().get(0).getProductName());
    }

    @Test
    void addItem_incrementsQuantity_whenProductAlreadyInCart() {
        Cart existingCart = new Cart();
        existingCart.setUserId(1L);
        CartItem existingItem = new CartItem();
        existingItem.setProductId(10L);
        existingItem.setProductName("Laptop");
        existingItem.setPrice(1000.0);
        existingItem.setQuantity(1);
        existingCart.getItems().add(existingItem);
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(existingCart));

        ProductDto product = new ProductDto();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        when(productServiceClient.getProductById(10L)).thenReturn(product);

        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setProductId(10L);
        requestDto.setQuantity(2);

        Cart result = cartService.addItem(1L, requestDto);

        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getQuantity());
        assertEquals(3000.0, result.getTotalPrice());
    }

    @Test
    void addItem_throwsInvalidQuantityException_whenQuantityIsZero() {
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setProductId(10L);
        requestDto.setQuantity(0);

        assertThrows(InvalidQuantityException.class, () -> cartService.addItem(1L, requestDto));
    }

    @Test
    void updateItemQuantity_throwsCartNotFoundException_whenNoCart() {
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.updateItemQuantity(1L, 10L, 5));
    }

}
