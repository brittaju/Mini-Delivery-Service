package brittajudelivery.domain;

import brittajucommon.http.kafka.DeliveryAssignedEvent;
import brittajucommon.http.kafka.OrderPaidEvent;
import brittajudelivery.database.DeliveryEntity;
import brittajudelivery.database.DeliveryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryEntityRepository repository;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    public void processOrderPaid(OrderPaidEvent event) {
        Optional<DeliveryEntity> optionalEntity = repository.findByOrderId(event.orderId());
        if(optionalEntity.isPresent()) {
            return;
        }
        DeliveryEntity entity = optionalEntity.get();
        entity.setOrderId(event.orderId());
        entity.setCourierName("Andrey");
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 60));
        repository.save(entity);
        sendDeliveryAssignedEvent(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity entity) {
        kafkaTemplate.send(
                "deliveries.topic",
                entity.getOrderId(),
                DeliveryAssignedEvent
                        .builder()
                        .courierName(entity.getCourierName())
                        .orderId(entity.getOrderId())
                        .etaMinutes(entity.getEtaMinutes())
                        .build()
        );
    }
}
