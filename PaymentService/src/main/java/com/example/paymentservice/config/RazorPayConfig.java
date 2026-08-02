package com.example.paymentservice.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorPayConfig {

    // @Value annotation helps to directly config values
    // from application.properties. We are reading razorpay.key.id.
    @Value("${razorpay.key.id}")
    private String razorpayId;

    // @Value annotation helps to directly config values
    // from application.properties. We are reading razorpay.key.secret.
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayId, razorpaySecret);
    }
}
