package com.example.ordermanagementservice.clients;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// thin kafka client used to publish events (e.g. "order.placed") for other services to consume
@Component
public class KafkaClient {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaClient(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
