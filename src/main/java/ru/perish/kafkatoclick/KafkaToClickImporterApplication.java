package ru.perish.kafkatoclick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.perish.kafkatoclick.config.ImporterProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImporterProperties.class)
public class KafkaToClickImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaToClickImporterApplication.class, args);
	}

}
