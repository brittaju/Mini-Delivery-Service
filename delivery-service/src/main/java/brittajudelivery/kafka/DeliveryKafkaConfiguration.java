package brittajudelivery.kafka;

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
public class DeliveryKafkaConfiguration {

    @Bean
    public ConsumerFactory<Long, OrderPaidEvent> orderPaidEventConsumerFactory(
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
            ConsumerFactory<Long, OrderPaidEvent> orderPaidEventConsumerFactory
    ){
        ConcurrentKafkaListenerContainerFactory<Long, OrderPaidEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderPaidEventConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    public DefaultKafkaProducerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventProducerFactory(
            KafkaProperties properties
    ) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<Long, DeliveryAssignedEvent> orderPaidEventKafkaTemplate(
            DefaultKafkaProducerFactory<Long, DeliveryAssignedEvent> deliveryAssignedEventProducerFactory
    ){
        return new KafkaTemplate<>(deliveryAssignedEventProducerFactory);
    }
}
