package com.example.paymentservice.paymentgateway;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentLinkResult {
    private final String url;
    private final String gatewayReference;
}
