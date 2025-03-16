package ydx.practicum.app.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;
import ydx.practicum.app.config.KafkaProperties;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaTopicCreator {
    private final AdminClient adminClient;
    private final KafkaProperties kafkaProperties;

    public KafkaTopicCreator(AdminClient adminClient, KafkaProperties kafkaProperties) {
        this.adminClient = adminClient;
        this.kafkaProperties = kafkaProperties;
    }

    public void createTopicIfNotExist(String topicName){
        createTopicIfNotExist(topicName, kafkaProperties.getPartitions(), kafkaProperties.getReplicationFactor());
    }

    public void createTopicIfNotExist(String topicName, int partitions, short replicationFactor){
        try {
            // Получаем список всех топиков
            ListTopicsResult listTopicsResult = adminClient.listTopics();
            Set<String> existingTopics = listTopicsResult.names().get();

            // Проверяем, существует ли топик
            if (!existingTopics.contains(topicName)) {
                // Создаем топик, если он не существует
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Топик " + topicName + " успешно создан!");
            } else {
                System.out.println("Топик " + topicName + " уже существует.");
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Ошибка при проверке или создании топика: " + e.getMessage());
        }
    }
}
