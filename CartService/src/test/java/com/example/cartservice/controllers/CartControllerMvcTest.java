package com.example.cartservice.controllers;

import com.example.cartservice.dtos.AddToCartRequestDto;
import com.example.cartservice.exceptions.CartNotFoundException;
import com.example.cartservice.exceptions.InvalidQuantityException;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCart_returnsOkWithCartBody() throws Exception {
        Cart cart = new Cart();
        cart.setUserId(1L);
        cart.setTotalPrice(0.0);
        when(cartService.getCart(1L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalPrice").value(0.0));
    }

    @Test
    void addItem_returnsCreatedWithUpdatedCart() throws Exception {
        Cart cart = new Cart();
        cart.setUserId(1L);
        CartItem item = new CartItem();
        item.setProductId(10L);
        item.setProductName("Laptop");
        item.setPrice(1000.0);
        item.setQuantity(1);
        item.setSubtotal(1000.0);
        cart.getItems().add(item);
        cart.setTotalPrice(1000.0);
        when(cartService.addItem(anyLong(), any(AddToCartRequestDto.class))).thenReturn(cart);

        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setProductId(10L);
        requestDto.setQuantity(1);

        mockMvc.perform(post("/api/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(1000.0));
    }

    @Test
    void addItem_returnsBadRequest_whenQuantityInvalid() throws Exception {
        when(cartService.addItem(anyLong(), any(AddToCartRequestDto.class)))
                .thenThrow(new InvalidQuantityException("Quantity must be greater than 0"));

        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setProductId(10L);
        requestDto.setQuantity(0);

        mockMvc.perform(post("/api/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCart_returnsNotFound_whenCartMissing() throws Exception {
        when(cartService.getCart(1L)).thenThrow(new CartNotFoundException("Cart not found for userId: 1"));

        mockMvc.perform(get("/api/cart/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void clearCart_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart/1")).andExpect(status().isNoContent());
    }
}
