package com.ssafy.alrebaba.common.configuration.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.ssafy.alrebaba.chat.domain.Message;
import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBroker;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Message> chatKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Message> chatConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(Message.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, NotificationMessage> notificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, NotificationMessage> notificationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(NotificationMessage.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CRDTMessage> crdtKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CRDTMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(crdtConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CRDTMessage> crdtConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(CRDTMessage.class));
    }



    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configs;
    }
}
