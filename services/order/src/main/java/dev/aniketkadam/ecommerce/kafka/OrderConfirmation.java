package dev.aniketkadam.ecommerce.kafka;

import dev.aniketkadam.ecommerce.customer.CustomerResponse;
import dev.aniketkadam.ecommerce.order.PaymentMethod;
import dev.aniketkadam.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
   String orderReference,
   BigDecimal totalAmount,
   PaymentMethod paymentMethod,
   CustomerResponse customer,
   List<PurchaseResponse> products
) {}
