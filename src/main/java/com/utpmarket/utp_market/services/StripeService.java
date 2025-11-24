package com.utpmarket.utp_market.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createAndConfirmPayment(String paymentMethodId, Double amount, String currency, String email)
            throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency(currency)
                .setReceiptEmail(email)
                .setDescription("Compra en UTP Market")
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setReturnUrl("http://localhost:8080/carrito/pedido-confirmacion")
                .build();

        return PaymentIntent.create(params);
    }

    public PaymentIntent createPaymentIntent(Double amount, String currency, String email) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency(currency)
                .setReceiptEmail(email)
                .setDescription("Compra en UTP Market")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        return PaymentIntent.create(params);
    }
}