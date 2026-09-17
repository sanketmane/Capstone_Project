package com.example.paymentservice.services;

import com.example.paymentservice.clients.KafkaClient;
import com.example.paymentservice.clients.OrderServiceClient;
import com.example.paymentservice.dtos.PaymentGatewayType;
import com.example.paymentservice.models.Payment;
import com.example.paymentservice.models.PaymentStatus;
import com.example.paymentservice.paymentgateway.IPaymentGateway;
import com.example.paymentservice.paymentgateway.PaymentGatewayChooserStrategy;
import com.example.paymentservice.paymentgateway.PaymentLinkResult;
import com.example.paymentservice.repos.PaymentRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private KafkaClient kafkaClient;

    @Mock
    private IPaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "objectMapper", new ObjectMapper());
    }

    @Test
    void getPaymentLink_defaultsToStripe_whenGatewayNotSpecified() {
        when(paymentGatewayChooserStrategy.getPaymentGateway(PaymentGatewayType.STRIPE)).thenReturn(paymentGateway);
        when(paymentGateway.generatePaymentLink(100L, "order1", "9999999999", "name", "email"))
                .thenReturn(new PaymentLinkResult("https://pay.example.com/link", "ref-1"));
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String url = paymentService.getPaymentLink(100L, "order1", "9999999999", "name", "email", null);

        assertEquals("https://pay.example.com/link", url);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepo).save(captor.capture());
        assertEquals(PaymentGatewayType.STRIPE, captor.getValue().getGateway());
        assertEquals(PaymentStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void getPaymentLink_usesRazorpay_whenExplicitlyRequested() {
        when(paymentGatewayChooserStrategy.getPaymentGateway(PaymentGatewayType.RAZORPAY)).thenReturn(paymentGateway);
        when(paymentGateway.generatePaymentLink(100L, "order1", "9999999999", "name", "email"))
                .thenReturn(new PaymentLinkResult("https://razorpay.example.com/link", "ref-2"));
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String url = paymentService.getPaymentLink(100L, "order1", "9999999999", "name", "email",
                PaymentGatewayType.RAZORPAY);

        assertEquals("https://razorpay.example.com/link", url);
    }

    @Test
    void handlePaymentSuccess_updatesPaymentOrderAndPublishesEvent() {
        Payment payment = new Payment();
        payment.setOrderId("1");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setEmail("email@example.com");
        payment.setAmount(100L);
        payment.setGateway(PaymentGatewayType.STRIPE);
        when(paymentRepo.findByOrderId("1")).thenReturn(Optional.of(payment));
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.handlePaymentSuccess("1", "session-123");

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals("session-123", payment.getGatewayReference());
        verify(orderServiceClient).updateOrderStatus(1L, "COMPLETED");
        verify(kafkaClient).sendMessage(anyString(), anyString());
    }

    @Test
    void handlePaymentSuccess_isIdempotent_whenAlreadySucceeded() {
        Payment payment = new Payment();
        payment.setOrderId("order1");
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepo.findByOrderId("order1")).thenReturn(Optional.of(payment));

        paymentService.handlePaymentSuccess("order1", "session-123");

        verify(orderServiceClient, never()).updateOrderStatus(any(), anyString());
        verify(kafkaClient, never()).sendMessage(anyString(), anyString());
    }

    @Test
    void handlePaymentFailure_marksPaymentFailed_andDoesNotTouchOrderOrKafka() {
        Payment payment = new Payment();
        payment.setOrderId("order1");
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepo.findByOrderId("order1")).thenReturn(Optional.of(payment));
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.handlePaymentFailure("order1");

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        verify(orderServiceClient, never()).updateOrderStatus(any(), anyString());
        verify(kafkaClient, never()).sendMessage(anyString(), anyString());
    }

    @Test
    void handlePaymentFailure_noOp_whenPaymentNotFound() {
        when(paymentRepo.findByOrderId("missing")).thenReturn(Optional.empty());

        paymentService.handlePaymentFailure("missing");

        verify(paymentRepo, never()).save(any());
    }
}
