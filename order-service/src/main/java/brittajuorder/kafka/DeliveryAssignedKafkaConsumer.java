package brittajuorder.kafka;

import brittajucommon.http.kafka.DeliveryAssignedEvent;
import brittajuorder.domain.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class DeliveryAssignedKafkaConsumer {

    private final OrderService service;

    @KafkaListener(
            topics = "deliveries.topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        service.processDeliveryAssigned(event);
    }
}
