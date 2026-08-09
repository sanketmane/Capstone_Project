package com.example.cartservice.controllers;

import com.example.cartservice.dtos.AddToCartRequestDto;
import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.dtos.CheckoutRequestDto;
import com.example.cartservice.dtos.OrderDto;
import com.example.cartservice.dtos.UpdateCartItemRequestDto;
import com.example.cartservice.mappers.CartMapper;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.Order;
import com.example.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable("userId") Long userId) {
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(CartMapper.toDto(cart));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartDto> addItem(@PathVariable("userId") Long userId,
                                            @RequestBody AddToCartRequestDto requestDto) {
        Cart cart = cartService.addItem(userId, requestDto);
        return new ResponseEntity<>(CartMapper.toDto(cart), HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDto> updateItemQuantity(@PathVariable("userId") Long userId,
                                                       @PathVariable("productId") Long productId,
                                                       @RequestBody UpdateCartItemRequestDto requestDto) {
        Cart cart = cartService.updateItemQuantity(userId, productId, requestDto.getQuantity());
        return ResponseEntity.ok(CartMapper.toDto(cart));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable("userId") Long userId,
                                               @PathVariable("productId") Long productId) {
        Cart cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(CartMapper.toDto(cart));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable("userId") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build(); // build() method is used to create a ResponseEntity with no content.
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderDto> checkout(@PathVariable("userId") Long userId,
                                              @RequestBody CheckoutRequestDto checkoutRequestDto) {
        Order order = cartService.checkout(userId, checkoutRequestDto);
        return ResponseEntity.ok(CartMapper.toDto(order));
    }
}
