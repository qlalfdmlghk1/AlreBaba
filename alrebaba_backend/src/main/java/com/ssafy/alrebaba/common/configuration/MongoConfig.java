package com.ssafy.alrebaba.common.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.client}")
    private String client;

    @Value("${spring.data.mongodb.name}")
    private String name;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(client);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), name);
    }

}
