package ydx.practicum.app.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ydx.practicum.app.config.KafkaProperties;
import ydx.practicum.app.model.Message;

@Service
public class MessageService {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public MessageService(KafkaTemplate<String, Message> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void sendMessage(String userTo, String userFrom, String message) {
        Message userMessage = new Message(userFrom, message);
        kafkaTemplate.send(kafkaProperties.getTopicForMessagesName(), userTo, userMessage);
        System.out.println("Сообщение отправлено: " + userMessage);
    }
}
