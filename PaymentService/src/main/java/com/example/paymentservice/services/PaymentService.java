package com.example.paymentservice.services;

import com.example.paymentservice.clients.KafkaClient;
import com.example.paymentservice.clients.OrderServiceClient;
import com.example.paymentservice.dtos.PaymentCompletedEventDto;
import com.example.paymentservice.dtos.PaymentGatewayType;
import com.example.paymentservice.models.Payment;
import com.example.paymentservice.models.PaymentStatus;
import com.example.paymentservice.paymentgateway.IPaymentGateway;
import com.example.paymentservice.paymentgateway.PaymentGatewayChooserStrategy;
import com.example.paymentservice.paymentgateway.PaymentLinkResult;
import com.example.paymentservice.repos.PaymentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment.completed";

    @Autowired
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private KafkaClient kafkaClient;

    @Autowired
    private ObjectMapper objectMapper;

    public String getPaymentLink(
        Long amount, 
        String orderId, 
        String phoneNumber, 
        String name, 
        String email, 
        PaymentGatewayType gateway
    ) {
        PaymentGatewayType effectiveGateway = gateway != null ? gateway : PaymentGatewayType.STRIPE;
        IPaymentGateway paymentGateway = paymentGatewayChooserStrategy.getPaymentGateway(effectiveGateway);
        PaymentLinkResult result = paymentGateway.generatePaymentLink(amount, orderId, phoneNumber, name, email);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setGateway(effectiveGateway);
        payment.setAmount(amount);
        payment.setEmail(email);
        payment.setName(name);
        payment.setPhoneNumber(phoneNumber);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayReference(result.getGatewayReference());
        paymentRepo.save(payment);

        return result.getUrl();
    }

    // called by webhook controllers once the gateway confirms a successful payment; idempotent on retries
    public void handlePaymentSuccess(String orderId, String gatewayReference) {
        Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);
        if (payment == null || payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setGatewayReference(gatewayReference);
        paymentRepo.save(payment);

        orderServiceClient.updateOrderStatus(Long.parseLong(orderId), "COMPLETED");
        publishPaymentCompletedEvent(payment);
    }

    // called by webhook controllers when a session/payment link expires or fails; order stays PENDING for retry
    public void handlePaymentFailure(String orderId) {
        Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);
        if (payment == null || payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepo.save(payment);
    }

    private void publishPaymentCompletedEvent(Payment payment) {
        PaymentCompletedEventDto event = new PaymentCompletedEventDto();
        event.setOrderId(payment.getOrderId());
        event.setEmail(payment.getEmail());
        event.setAmount(payment.getAmount());
        event.setGateway(payment.getGateway());
        event.setGatewayReference(payment.getGatewayReference());
        try {
            kafkaClient.sendMessage(PAYMENT_COMPLETED_TOPIC, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}

