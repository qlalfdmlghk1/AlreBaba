package com.ssafy.alrebaba.common.configuration.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.ssafy.alrebaba.chat.domain.Message;
import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBroker;

    @Bean
    public KafkaTemplate<String, Message> chatKafkaTemplate() {
        return new KafkaTemplate<>(chatProducerFactory());
    }


    @Bean
    public ProducerFactory<String, Message> chatProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, NotificationMessage> notificationKafkaTemplate() {
        return new KafkaTemplate<>(notificationProducerFactory());
    }

    @Bean
    public ProducerFactory<String, NotificationMessage> notificationProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configs;
    }

    @Bean
    public ProducerFactory<String, CRDTMessage> crdtProducerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, CRDTMessage> crdtKafkaTemplate() {
        return new KafkaTemplate<>(crdtProducerFactory());
    }


    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }
}
