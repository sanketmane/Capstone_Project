package com.example.paymentservice.paymentgateway;

import com.example.paymentservice.dtos.PaymentGatewayType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayChooserStrategy {

    @Autowired
    private RazorPaymentGateway razorPaymentGateway;

    @Autowired
    private StripePaymentGateway stripePaymentGateway;

    // null gateway defaults to STRIPE to preserve previous hardcoded behavior
    public IPaymentGateway getPaymentGateway(PaymentGatewayType gateway) {
        if (gateway == PaymentGatewayType.RAZORPAY) {
            return razorPaymentGateway;
        }
        return stripePaymentGateway;
    }
}
