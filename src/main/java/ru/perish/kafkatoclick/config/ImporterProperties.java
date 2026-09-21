package ru.perish.kafkatoclick.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "importer")
public record ImporterProperties(String topic, String table) {
}
