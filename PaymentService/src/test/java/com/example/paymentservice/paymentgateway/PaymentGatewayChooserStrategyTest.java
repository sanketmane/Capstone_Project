package com.example.paymentservice.paymentgateway;

import com.example.paymentservice.dtos.PaymentGatewayType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayChooserStrategyTest {

    @Mock
    private RazorPaymentGateway razorPaymentGateway;

    @Mock
    private StripePaymentGateway stripePaymentGateway;

    @InjectMocks
    private PaymentGatewayChooserStrategy paymentGatewayChooserStrategy;

    @Test
    void getPaymentGateway_returnsRazorpay_whenRazorpayRequested() {
        IPaymentGateway result = paymentGatewayChooserStrategy.getPaymentGateway(PaymentGatewayType.RAZORPAY);

        assertSame(razorPaymentGateway, result);
    }

    @Test
    void getPaymentGateway_returnsStripe_whenStripeRequested() {
        IPaymentGateway result = paymentGatewayChooserStrategy.getPaymentGateway(PaymentGatewayType.STRIPE);

        assertSame(stripePaymentGateway, result);
    }

    @Test
    void getPaymentGateway_defaultsToStripe_whenGatewayIsNull() {
        IPaymentGateway result = paymentGatewayChooserStrategy.getPaymentGateway(null);

        assertSame(stripePaymentGateway, result);
    }
}
