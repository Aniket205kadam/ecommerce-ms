package dev.aniketkadam.ecommerce.payment;

import dev.aniketkadam.ecommerce.customer.CustomerResponse;
import dev.aniketkadam.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {}
