package brittajudelivery.kafka;

import brittajucommon.http.kafka.OrderPaidEvent;
import brittajudelivery.domain.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class OrderPaidKafkaConsumer {

    private final DeliveryService service;

    @KafkaListener(
            topics = "orders.topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(OrderPaidEvent event) {
        service.processOrderPaid(event);
    }
}
