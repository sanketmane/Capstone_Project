package com.example.emailservice.consumers;

import com.example.emailservice.dtos.EmailDto;
import com.example.emailservice.dtos.OrderPlacedEventDto;
import com.example.emailservice.dtos.PaymentCompletedEventDto;
import com.example.emailservice.util.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Component
public class EmailConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    // @KafkaListener annotation helps to declare a method
    // that will be called when a topic arrives at the consumer side.
    // groupId is used when there are multiple instances like ec2
    // which can receive same topic at a given time and can generate multiple emails.
    // To avoid that, groupId selects only 1 instance to do the actual work.
    @KafkaListener(topics = "signup", groupId = "emailService")
    public void sendEmail(String message) {
        sendEmailFromDto(message);
    }

    // password-reset carries a pre-formatted EmailDto (same shape as signup) with the reset link in the body
    @KafkaListener(topics = "password-reset", groupId = "emailService")
    public void sendPasswordResetEmail(String message) {
        sendEmailFromDto(message);
    }

    private void sendEmailFromDto(String message) {
        try {
            EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);
            Session session = buildSession();
            EmailUtil.sendEmail(session, emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());
        }catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    // order.placed carries raw order fields, not a pre-formatted EmailDto, so compose the email here
    @KafkaListener(topics = "order.placed", groupId = "emailService")
    public void sendOrderConfirmationEmail(String message) {
        try {
            OrderPlacedEventDto event = objectMapper.readValue(message, OrderPlacedEventDto.class);
            String subject = "Order Confirmation - Order #" + event.getOrderId();
            String body = "Your order has been placed successfully.\n"
                    + "Order #" + event.getOrderId() + "\n"
                    + "Items: " + event.getItemCount() + "\n"
                    + "Total: " + event.getTotalAmount();
            Session session = buildSession();
            EmailUtil.sendEmail(session, event.getEmail(), subject, body);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    // payment.completed carries raw payment fields, published by PaymentService after webhook confirmation
    @KafkaListener(topics = "payment.completed", groupId = "emailService")
    public void sendPaymentConfirmationEmail(String message) {
        try {
            PaymentCompletedEventDto event = objectMapper.readValue(message, PaymentCompletedEventDto.class);
            String subject = "Payment Successful - Order #" + event.getOrderId();
            String body = "Your payment has been received successfully.\n"
                    + "Order #" + event.getOrderId() + "\n"
                    + "Amount: " + event.getAmount() + "\n"
                    + "Gateway: " + event.getGateway();
            Session session = buildSession();
            EmailUtil.sendEmail(session, event.getEmail(), subject, body);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    private Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailPassword);
            }
        };
        return Session.getInstance(props, auth); // establish email session
    }
}
