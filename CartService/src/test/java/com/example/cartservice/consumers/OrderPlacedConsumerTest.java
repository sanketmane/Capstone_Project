package com.example.cartservice.consumers;

import com.example.cartservice.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderPlacedConsumerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderPlacedConsumer orderPlacedConsumer;

    @BeforeEach
    void setUp() {
        // @Autowired ObjectMapper field isn't a @Mock, so wire a real one manually
        ReflectionTestUtils.setField(orderPlacedConsumer, "objectMapper", new ObjectMapper());
    }

    @Test
    void handleOrderPlaced_clearsCartForEventUserId() {
        String message = "{\"orderId\":1,\"userId\":1,\"email\":\"user@example.com\","
                + "\"totalAmount\":1000.0,\"itemCount\":1}";

        orderPlacedConsumer.handleOrderPlaced(message);

        verify(cartService).clearCart(1L);
    }
}
