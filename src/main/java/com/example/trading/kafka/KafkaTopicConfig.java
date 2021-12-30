package com.example.trading.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic bankCreated() {
        return new NewTopic("bank-created", 1, (short) 1);
    }

    @Bean
    public NewTopic currentItemPrices() {
        return new NewTopic("current-item-prices", 1, (short) 1);
    }

    @Bean
    public NewTopic currentResourcePrices() {
        return new NewTopic("current-resource-prices", 1, (short) 1);
    }

    @Bean
    public NewTopic trades() {
        return new NewTopic("trades", 1, (short) 1);
    }
}
