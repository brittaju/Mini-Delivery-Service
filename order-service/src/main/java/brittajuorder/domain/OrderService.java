package brittajuorder.domain;

import brittajucommon.http.kafka.DeliveryAssignedEvent;
import brittajucommon.http.kafka.OrderPaidEvent;
import brittajucommon.http.order.OrderRequestDto;
import brittajucommon.http.order.OrderStatus;
import brittajucommon.http.payment.PaymentRequestDto;
import brittajucommon.http.payment.PaymentResponseDto;
import brittajucommon.http.payment.PaymentStatus;
import brittajuorder.api.OrderPaymentRequest;
import brittajuorder.database.OrderEntity;
import brittajuorder.database.OrderEntityMapper;
import brittajuorder.database.OrderItemEntity;
import brittajuorder.database.OrderRepository;
import brittajuorder.external.PaymentHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper mapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    public OrderEntity create(OrderRequestDto request) {
        OrderEntity entity = mapper.toEntity(request);
        calculatePricing(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return orderRepository.save(entity);
    }

    public OrderEntity getOne(Long id) {
        Optional<OrderEntity> entity = orderRepository.findById(id);
        return entity
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Not found:" + id));
    }

    private void calculatePricing(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item : entity.getItems()) {
            double randomPrice = ThreadLocalRandom
                    .current()
                    .nextDouble(100, 1000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));
            totalPrice = item
                    .getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(totalPrice);
        }
        entity.setTotalAmount(totalPrice);
    }

    public OrderEntity processPayment(
            Long id,
            OrderPaymentRequest request
    ) {
        OrderEntity entity = getOne(id);
        if (!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new RuntimeException(
                    "Status must be: PENDING_PAYMENT");
        }
        PaymentResponseDto response = paymentHttpClient
                .createPayment(PaymentRequestDto
                        .builder()
                        .orderId(id)
                        .paymentMethod(request.paymentMethod())
                        .amount(entity.getTotalAmount())
                        .build());
        OrderStatus status = response
                .paymentStatus()
                .equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;
        entity.setOrderStatus(status);
        orderRepository.save(entity);
        if (status.equals(OrderStatus.PAID)) {
            sendOrderPaidEvent(entity, response);
        }
        return entity;
    }

    private void sendOrderPaidEvent(
            OrderEntity entity,
            PaymentResponseDto response
    ) {
        kafkaTemplate.send(
                "orders.event",
                entity.getId(),
                OrderPaidEvent
                        .builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .build()
        );
    }

    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        OrderEntity entity = getOne(event.orderId());
        if (!entity.getOrderStatus().equals(OrderStatus.PAID)) {
            return;
        }
        entity.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        entity.setCourierName(event.courierName());
        entity.setEtaMinutes(event.etaMinutes());
        orderRepository.save(entity);
    }
}
