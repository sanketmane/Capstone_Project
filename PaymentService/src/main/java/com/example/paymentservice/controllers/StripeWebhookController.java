package com.example.paymentservice.controllers;

import com.example.paymentservice.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payments/webhook")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // Stripe calls this on checkout session completion/expiration; payload signature must be verified
    // before trusting anything in it, since this endpoint is publicly reachable.
    @PostMapping("/stripe")
    public ResponseEntity<Void> listenToEvents(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        switch (event.getType()) {
            case "checkout.session.completed" -> handleCompleted(event, payload);
            case "checkout.session.expired" -> handleExpired(event, payload);
            default -> { /* ignore unrelated event types */ }
        }
        return ResponseEntity.ok().build();
    }

    private void handleCompleted(Event event, String payload) {
        String orderId = getSessionField(payload, "metadata", "orderId");
        String paymentLinkId = getSessionField(payload, "payment_link");
        String sessionId = getSessionField(payload, "id");
        logger.info("Stripe checkout completed: eventId={}, sessionId={}, paymentLinkId={}, orderId={}",
                event.getId(), sessionId, paymentLinkId, orderId);
        if (orderId != null) {
            paymentService.handlePaymentSuccess(orderId, sessionId);
        } else if (paymentLinkId != null) {
            paymentService.handlePaymentSuccessByGatewayReference(paymentLinkId);
        } else {
            logger.warn("Stripe checkout completion has no payment correlation fields: eventId={}", event.getId());
        }
    }

    private void handleExpired(Event event, String payload) {
        String orderId = getSessionField(payload, "metadata", "orderId");
        String paymentLinkId = getSessionField(payload, "payment_link");
        if (orderId != null) {
            paymentService.handlePaymentFailure(orderId);
        } else if (paymentLinkId != null) {
            paymentService.handlePaymentFailureByGatewayReference(paymentLinkId);
        } else {
            logger.warn("Stripe checkout expiration has no payment correlation fields: eventId={}", event.getId());
        }
    }

    private String getSessionField(String payload, String... fields) {
        try {
            JsonNode node = objectMapper.readTree(payload).path("data").path("object");
            for (String field : fields) {
                node = node.path(field);
            }
            return node.isTextual() ? node.asText() : null;
        } catch (Exception exception) {
            logger.warn("Unable to read Stripe checkout field from webhook payload", exception);
            return null;
        }
    }

}

