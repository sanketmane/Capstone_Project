package com.example.paymentservice.paymentgateway;

public interface IPaymentGateway {

    PaymentLinkResult generatePaymentLink(Long amount, String orderId, String phoneNumber, String name, String email);
}
