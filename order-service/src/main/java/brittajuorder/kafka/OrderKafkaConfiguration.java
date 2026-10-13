package brittajuorder.kafka;

import brittajucommon.http.kafka.DeliveryAssignedEvent;
import brittajucommon.http.kafka.OrderPaidEvent;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.Map;

@Configuration
public class OrderKafkaConfiguration {

    @Bean
    public DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventProducerFactory(
            KafkaProperties properties
    ) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<Long, OrderPaidEvent> orderPaidEventKafkaTemplate(
            DefaultKafkaProducerFactory<Long, OrderPaidEvent> orderPaidEventProducerFactory
    ){
        return new KafkaTemplate<>(orderPaidEventProducerFactory);
    }

    @Bean
    public ConsumerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory(
            KafkaProperties properties
    ) {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProperties.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "brittaju.api.kafka");
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(
            ConsumerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory
    ){
        ConcurrentKafkaListenerContainerFactory<Long, DeliveryAssignedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryAssignedEventConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }
}
