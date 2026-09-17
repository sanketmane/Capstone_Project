package com.example.paymentservice.controllers;

import com.example.paymentservice.services.PaymentService;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
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
public class RazorpayWebhookController {

    @Autowired
    private PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    // Razorpay calls this on payment link lifecycle events; payload signature must be verified
    // before trusting anything in it, since this endpoint is publicly reachable.
    @PostMapping("/razorpay")
    public ResponseEntity<Void> listenToEvents(@RequestBody String payload,
                                                @RequestHeader("X-Razorpay-Signature") String signatureHeader) {
        try {
            if (!Utils.verifyWebhookSignature(payload, signatureHeader, webhookSecret)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (RazorpayException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        JSONObject body = new JSONObject(payload);
        String eventType = body.optString("event");
        switch (eventType) {
            case "payment_link.paid" -> handlePaid(body);
            case "payment_link.expired", "payment_link.cancelled" -> handleExpired(body);
            default -> { /* ignore unrelated event types */ }
        }
        return ResponseEntity.ok().build();
    }

    private void handlePaid(JSONObject body) {
        String orderId = extractReferenceId(body);
        String paymentLinkId = extractPaymentLinkId(body);
        if (orderId != null) {
            paymentService.handlePaymentSuccess(orderId, paymentLinkId);
        }
    }

    private void handleExpired(JSONObject body) {
        String orderId = extractReferenceId(body);
        if (orderId != null) {
            paymentService.handlePaymentFailure(orderId);
        }
    }

    private String extractReferenceId(JSONObject body) {
        JSONObject entity = paymentLinkEntity(body);
        return entity != null && !entity.isNull("reference_id") ? entity.optString("reference_id") : null;
    }

    private String extractPaymentLinkId(JSONObject body) {
        JSONObject entity = paymentLinkEntity(body);
        return entity != null ? entity.optString("id") : null;
    }

    private JSONObject paymentLinkEntity(JSONObject body) {
        JSONObject payload = body.optJSONObject("payload");
        JSONObject paymentLink = payload != null ? payload.optJSONObject("payment_link") : null;
        return paymentLink != null ? paymentLink.optJSONObject("entity") : null;
    }
}
