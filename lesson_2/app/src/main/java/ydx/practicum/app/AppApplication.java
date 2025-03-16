package ydx.practicum.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ydx.practicum.app.config.KafkaProperties;
import ydx.practicum.app.service.KafkaTopicCreator;

@EnableKafkaStreams
@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	public CommandLineRunner startApp(KafkaTopicCreator kafkaTopicCreator, KafkaProperties kafkaProperties){
		return args -> {
            String messageTopicName = kafkaProperties.getTopicForMessagesName();
            int partitions = kafkaProperties.getPartitions();
            short replicationFactor = kafkaProperties.getReplicationFactor();
			kafkaTopicCreator.createTopicIfNotExist(messageTopicName, partitions, replicationFactor);
			kafkaTopicCreator.createTopicIfNotExist(kafkaProperties.getTopicForBlockUser(), partitions, replicationFactor);
			kafkaTopicCreator.createTopicIfNotExist(kafkaProperties.getTopicForBlacklistWord(), partitions, replicationFactor);
			kafkaTopicCreator.createTopicIfNotExist(kafkaProperties.getTopicForBlockedMessages(), partitions, replicationFactor);
        };
    }

}
