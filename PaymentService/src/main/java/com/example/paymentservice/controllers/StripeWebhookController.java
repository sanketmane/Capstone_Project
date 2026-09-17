package com.example.paymentservice.controllers;

import com.example.paymentservice.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/webhook")
public class StripeWebhookController {

    @Autowired
    private PaymentService paymentService;

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
            case "checkout.session.completed" -> handleCompleted(event);
            case "checkout.session.expired" -> handleExpired(event);
            default -> { /* ignore unrelated event types */ }
        }
        return ResponseEntity.ok().build();
    }

    private void handleCompleted(Event event) {
        Session session = extractSession(event);
        if (session == null) {
            return;
        }
        String orderId = session.getMetadata().get("orderId");
        if (orderId != null) {
            paymentService.handlePaymentSuccess(orderId, session.getId());
        }
    }

    private void handleExpired(Event event) {
        Session session = extractSession(event);
        if (session == null) {
            return;
        }
        String orderId = session.getMetadata().get("orderId");
        if (orderId != null) {
            paymentService.handlePaymentFailure(orderId);
        }
    }

    private Session extractSession(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        return stripeObject instanceof Session session ? session : null;
    }
}

