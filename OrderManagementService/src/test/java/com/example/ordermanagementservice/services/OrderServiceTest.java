package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.CartServiceClient;
import com.example.ordermanagementservice.clients.KafkaClient;
import com.example.ordermanagementservice.dtos.CartDto;
import com.example.ordermanagementservice.dtos.CartItemDto;
import com.example.ordermanagementservice.dtos.DeliveryAddressDto;
import com.example.ordermanagementservice.dtos.PlaceOrderRequestDto;
import com.example.ordermanagementservice.exceptions.EmptyCartException;
import com.example.ordermanagementservice.exceptions.OrderNotFoundException;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderStatus;
import com.example.ordermanagementservice.models.PaymentMethod;
import com.example.ordermanagementservice.repos.OrderRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private CartServiceClient cartServiceClient;

    @Mock
    private KafkaClient kafkaClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        // @Autowired ObjectMapper field isn't a @Mock, so wire a real one manually
        org.springframework.test.util.ReflectionTestUtils.setField(orderService, "objectMapper", new ObjectMapper());
    }

    private CartDto cartWithOneItem() {
        CartItemDto item = new CartItemDto();
        item.setProductId(10L);
        item.setProductName("Laptop");
        item.setPrice(1000.0);
        item.setQuantity(1);
        item.setSubtotal(1000.0);

        CartDto cart = new CartDto();
        cart.setUserId(1L);
        cart.setItems(new ArrayList<>(List.of(item)));
        cart.setTotalPrice(1000.0);
        return cart;
    }

    private PlaceOrderRequestDto placeOrderRequest() {
        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto();
        requestDto.setUserId(1L);
        requestDto.setPaymentMethod(PaymentMethod.CARD);
        requestDto.setDeliveryAddress(new DeliveryAddressDto());
        requestDto.setEmail("user@example.com");
        return requestDto;
    }

    @Test
    void placeOrder_throwsEmptyCartException_whenCartHasNoItems() {
        CartDto emptyCart = new CartDto();
        emptyCart.setUserId(1L);
        emptyCart.setItems(new ArrayList<>());
        when(cartServiceClient.getCart(1L)).thenReturn(emptyCart);

        assertThrows(EmptyCartException.class, () -> orderService.placeOrder(placeOrderRequest()));
    }

    @Test
    void placeOrder_savesOrderAndPublishesEvent() {
        when(cartServiceClient.getCart(1L)).thenReturn(cartWithOneItem());
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });

        Order savedOrder = orderService.placeOrder(placeOrderRequest());

        assertEquals(1000.0, savedOrder.getTotalAmount());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        verify(orderRepo).save(any(Order.class));
        verify(kafkaClient).sendMessage(eq("order.placed"), any(String.class));
    }

    @Test
    void getOrderById_throwsOrderNotFoundException_whenOrderMissing() {
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getOrderById_returnsOrder_whenFound() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderHistory_returnsOrdersForUser() {
        Order order = new Order();
        order.setUserId(1L);
        when(orderRepo.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order));

        List<Order> history = orderService.getOrderHistory(1L);

        assertEquals(1, history.size());
        assertEquals(1L, history.get(0).getUserId());
    }

    @Test
    void updateOrderStatus_updatesAndSavesOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);

        assertEquals(OrderStatus.COMPLETED, result.getStatus());
    }
}
