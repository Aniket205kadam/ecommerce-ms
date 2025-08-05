package dev.aniketkadam.ecommerce.order;

import dev.aniketkadam.ecommerce.customer.CustomerClient;
import dev.aniketkadam.ecommerce.customer.CustomerResponse;
import dev.aniketkadam.ecommerce.exception.BusinessException;
import dev.aniketkadam.ecommerce.kafka.OrderConfirmation;
import dev.aniketkadam.ecommerce.kafka.OrderProducer;
import dev.aniketkadam.ecommerce.orderline.OrderLineRequest;
import dev.aniketkadam.ecommerce.orderline.OrderLineService;
import dev.aniketkadam.ecommerce.payment.PaymentClinet;
import dev.aniketkadam.ecommerce.payment.PaymentRequest;
import dev.aniketkadam.ecommerce.product.ProductClient;
import dev.aniketkadam.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClinet paymentClinet;

    public Integer createOrder(OrderRequest request) {
        CustomerResponse customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists with the provided ID::" + request.customerId()));
        var purchasedProducts = this.productClient.purchaseProducts(request.products());
        var order = this.orderRepository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClinet.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .toList();
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
